package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerAuthDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CustomerService {

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private CustomerAuthDao customerAuthDao;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity saveCustomer(CustomerEntity customer) throws SignUpRestrictedException {
        /* Validation checks */
        if (isContactNumberExist(customer))
            throw new SignUpRestrictedException("SGR-001",
                    "This contact number is already registered! Try other contact number.");
        if (!validateCustomer(customer))
            throw new SignUpRestrictedException("SGR-005", "Except last name all fields should be filled");
        if (!isValidEmailID(customer))
            throw new SignUpRestrictedException("SGR-002", "Invalid email-id format!");
        if (!isValidPhoneNumber(customer))
            throw new SignUpRestrictedException("SGR-003", "Invalid contact number!");
        if (!isValidPassword(customer.getPassword()))
            throw new SignUpRestrictedException("SGR-004", "Weak password!");
        /* Encrypt password */
        String[] encryptedText = passwordCryptographyProvider.encrypt(customer.getPassword());
        customer.setSalt(encryptedText[0]);
        customer.setPassword(encryptedText[1]);
        return customerDao.signupCustomer(customer);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthEntity authenticate(String username, String password)
            throws AuthenticationFailedException {

        CustomerEntity customerEntity = customerDao.getCustomerByContactNumber(username);
        System.out.println(customerEntity);
        if (customerEntity == null) {
            throw new AuthenticationFailedException(
                    "ATH-001", "This contact number has not been registered!");
        }
        final String encryptedPassword =
                PasswordCryptographyProvider.encrypt(password, customerEntity.getSalt());

        System.out.println(encryptedPassword);
        System.out.println("customer password" + customerEntity.getPassword());

//        if (!encryptedPassword.equals(customerEntity.getPassword())) {
//            throw new AuthenticationFailedException("ATH-002", "Invalid Credentials");
//        }
        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
        CustomerAuthEntity customerAuthEntity = new CustomerAuthEntity();
        customerAuthEntity.setUuid(UUID.randomUUID().toString());
        customerAuthEntity.setCustomer(customerEntity);
        final ZonedDateTime now = ZonedDateTime.now();
        final ZonedDateTime expiresAt = now.plusHours(8);
        customerAuthEntity.setLoginAt(now);
        customerAuthEntity.setExpiresAt(expiresAt);
        String accessToken = jwtTokenProvider.generateToken(customerEntity.getUuid(), now, expiresAt);
        customerAuthEntity.setAccessToken(accessToken);
        customerAuthDao.createCustomerAuthToken(customerAuthEntity);
        return customerAuthEntity;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthEntity logout(final String accessToken) throws AuthorizationFailedException {
        CustomerAuthEntity customerAuthEntity = customerAuthDao.getCustomerAuthByToken(accessToken);
        CustomerEntity customerEntity = getCustomer(accessToken);
        customerAuthEntity.setCustomer(customerEntity);
        customerAuthEntity.setLogoutAt(ZonedDateTime.now());
        customerAuthDao.updateCustomerAuthLogout(customerAuthEntity);
        return customerAuthEntity;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity updateCustomer(CustomerEntity customerEntity) {
        return customerDao.updateCustomer(customerEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity updateCustomerPassword(
            final String oldPassword, final String newPassword, final CustomerEntity customerEntity)
            throws UpdateCustomerException {
        if (isValidPassword(newPassword)) {
            String oldEncryptedPassword =
                    PasswordCryptographyProvider.encrypt(oldPassword, customerEntity.getSalt());
            if (!oldEncryptedPassword.equals(customerEntity.getPassword())) {
                throw new UpdateCustomerException("UCR-004", "Incorrect old password!");
            }
            String[] encryptedText = passwordCryptographyProvider.encrypt(newPassword);
            customerEntity.setSalt(encryptedText[0]);
            customerEntity.setPassword(encryptedText[1]);
            return customerDao.updateCustomer(customerEntity);
        } else {
            throw new UpdateCustomerException("UCR-001", "Weak password!");
        }
    }

    private boolean isContactNumberExist(CustomerEntity customer) {
        CustomerEntity customerEntity = customerDao.getCustomerByContactNumber(customer.getContactNumber());
        if (customerEntity != null) {
            return true;
        } else {
            return false;
        }
    }

    private boolean validateCustomer(CustomerEntity customer) {
        if (customer.getEmail().trim().length() == 0 || customer.getContactNumber().length() == 0
                || customer.getFirstName().length() == 0 || customer.getPassword().length() == 0)
            return false;
        return true;
    }

    private boolean isValidEmailID(CustomerEntity customer) {
        Pattern pattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(customer.getEmail());
        return matcher.find();
    }

    private boolean isValidPhoneNumber(CustomerEntity customer) {
        Pattern pattern = Pattern.compile("^[1-9]+\\d{9}$");
        Matcher matcher = pattern.matcher(customer.getContactNumber());
        return matcher.matches();
    }

    private boolean isValidPassword(String password) {
        Pattern p1 = Pattern.compile("^(?=.*\\d)(?=.*[A-Z])(?=.*\\W).*$");
        Matcher matcher = p1.matcher(password);
        return password.length() >= 8 && matcher.matches();
    }

    public CustomerEntity getCustomer(String accessToken) throws AuthorizationFailedException {
        CustomerAuthEntity customerAuthEntity = customerAuthDao.getCustomerAuthByToken(accessToken);
        if (customerAuthEntity != null) {

            if (customerAuthEntity.getLogoutAt() != null) {
                throw new AuthorizationFailedException(
                        "ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
            }

            if (ZonedDateTime.now().isAfter(customerAuthEntity.getExpiresAt())) {
                throw new AuthorizationFailedException(
                        "ATHR-003", "Your session is expired. Log in again to access this endpoint.");
            }
            return customerAuthEntity.getCustomer();
        } else {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        }
    }

}
