package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.common.Utility;
import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @CrossOrigin
    @RequestMapping(value = "customer/signup", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupCustomerResponse> signUpCustomer(
            @RequestBody SignupCustomerRequest signupCustomerRequest) throws SignUpRestrictedException {

        CustomerEntity customer = new CustomerEntity();

        customer.setUuid(UUID.randomUUID().toString());
        customer.setFirstName(signupCustomerRequest.getFirstName());
        customer.setLastName(signupCustomerRequest.getLastName());
        customer.setEmail(signupCustomerRequest.getEmailAddress());
        customer.setContactNumber(signupCustomerRequest.getContactNumber());
        customer.setPassword(signupCustomerRequest.getPassword());

        CustomerEntity result = customerService.saveCustomer(customer);

        return new ResponseEntity<SignupCustomerResponse>(
                new SignupCustomerResponse().id(result.getUuid()).status("CUSTOMER SUCCESSFULLY REGISTERED"),
                HttpStatus.CREATED);
    }

    @CrossOrigin
    @RequestMapping(path = "customer/login", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LoginResponse> login(
            @RequestHeader("authorization") final String authorization)
            throws AuthenticationFailedException {

        byte[] decode;
        String contactNumber;
        String password;
        try {
            decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
            String decodedText = new String(decode);
            String[] decodedArray = decodedText.split(":");
            contactNumber = decodedArray[0];
            password = decodedArray[1];
        } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException exception) {
            throw new AuthenticationFailedException(
                    "ATH-003", "Incorrect format of decoded customer name and password");
        }

        CustomerAuthEntity createdCustomerAuthEntity =
                customerService.authenticate(contactNumber, password);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setId(createdCustomerAuthEntity.getCustomer().getUuid());
        loginResponse.setFirstName(createdCustomerAuthEntity.getCustomer().getFirstName());
        loginResponse.setLastName(createdCustomerAuthEntity.getCustomer().getLastName());
        loginResponse.setContactNumber(createdCustomerAuthEntity.getCustomer().getContactNumber());
        loginResponse.setEmailAddress(createdCustomerAuthEntity.getCustomer().getEmail());
        loginResponse.setMessage("LOGGED IN SUCCESSFULLY");

        HttpHeaders headers = new HttpHeaders();
        headers.add("access-token", createdCustomerAuthEntity.getAccessToken());
        List<String> header = new ArrayList<>();
        header.add("access-token");
        headers.setAccessControlExposeHeaders(header);

        return new ResponseEntity<LoginResponse>(loginResponse, headers, HttpStatus.OK);
    }


    @CrossOrigin
    @RequestMapping(path = "customer/logout", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LogoutResponse> logout(@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {
        final String accessToken = Utility.getAccessTokenFromAuthorization(authorization);
        CustomerAuthEntity customerAuthEntity = customerService.logout(accessToken);

        LogoutResponse logoutResponse = new LogoutResponse().id(customerAuthEntity.getCustomer().getUuid()).message("LOGGED OUT SUCCESSFULLY");

        return new ResponseEntity<LogoutResponse>(logoutResponse, HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(path = "customer", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UpdateCustomerResponse> updateCustomer(@RequestBody UpdateCustomerRequest updateCustomerRequest, @RequestHeader("authorization") final String authorization) throws UpdateCustomerException, AuthorizationFailedException {

        if (updateCustomerRequest.getFirstName().isEmpty()) {
            throw new UpdateCustomerException("UCR-002", "First name field should not be empty");
        }

        String accessToken = Utility.getAccessTokenFromAuthorization(authorization);
        CustomerEntity customerEntity = customerService.getCustomer(accessToken);
        customerEntity.setFirstName(updateCustomerRequest.getFirstName());
        customerEntity.setLastName(updateCustomerRequest.getLastName());

        CustomerEntity updatedCustomerEntity = customerService.updateCustomer(customerEntity);

        UpdateCustomerResponse updateCustomerResponse = new UpdateCustomerResponse();
        updateCustomerResponse.setId(updatedCustomerEntity.getUuid());
        updateCustomerResponse.setFirstName(updatedCustomerEntity.getFirstName());
        updateCustomerResponse.setLastName(updatedCustomerEntity.getLastName());
        updateCustomerResponse.status("CUSTOMER DETAILS UPDATED SUCCESSFULLY");

        return new ResponseEntity<UpdateCustomerResponse>(updateCustomerResponse, HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(path = "customer/password", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UpdatePasswordResponse> updatePassword(@RequestBody(required = true) final UpdatePasswordRequest updatePasswordRequest, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, UpdateCustomerException {

        String oldPassword = updatePasswordRequest.getOldPassword();
        String newPassword = updatePasswordRequest.getNewPassword();

        if (oldPassword != null && newPassword != null) {
            String accessToken = Utility.getAccessTokenFromAuthorization(authorization);
            CustomerEntity customerEntity = customerService.getCustomer(accessToken);
            CustomerEntity updatedCustomerEntity = customerService.updateCustomerPassword(oldPassword, newPassword, customerEntity);

            UpdatePasswordResponse updatePasswordResponse =
                    new UpdatePasswordResponse()
                            .id(updatedCustomerEntity.getUuid())
                            .status("CUSTOMER PASSWORD UPDATED SUCCESSFULLY");

            return new ResponseEntity<UpdatePasswordResponse>(updatePasswordResponse, HttpStatus.OK);
        } else {
            throw new UpdateCustomerException("UCR-003", "No field should be empty");
        }
    }
}
