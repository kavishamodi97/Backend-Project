package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.AddressDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerAddressDao;
import com.upgrad.FoodOrderingApp.service.dao.StateDao;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class AddressService {

    @Autowired
    private AddressDao addressDao;

    @Autowired
    private CustomerAddressDao customerAddressDao;

    @Autowired
    private StateDao stateDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public AddressEntity saveAddress(
            final AddressEntity addressEntity, final CustomerEntity customerEntity)
            throws SaveAddressException {
        if (addressEntity.getActive() != null && addressEntity.getLocality() != null && !addressEntity.getLocality().isEmpty() && addressEntity.getCity() != null && !addressEntity.getCity().isEmpty() && addressEntity.getFlatBuilNo() != null
                && !addressEntity.getFlatBuilNo().isEmpty()
                && addressEntity.getPincode() != null
                && !addressEntity.getPincode().isEmpty()
                && addressEntity.getState() != null) {
            if (!isValidPinCode(addressEntity.getPincode())) {
                throw new SaveAddressException("SAR-002", "Invalid pincode");
            }
            AddressEntity SaveCustomerAddress = addressDao.createCustomerAddress(addressEntity);
            CustomerAddressEntity createdCustomerAddressEntity = new CustomerAddressEntity();
            createdCustomerAddressEntity.setCustomer(customerEntity);
            createdCustomerAddressEntity.setAddress(SaveCustomerAddress);
            customerAddressDao.createCustomerAddress(createdCustomerAddressEntity);
            return SaveCustomerAddress;
        } else {
            throw new SaveAddressException("SAR-001", "No field can be empty");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<AddressEntity> getAllAddress(final CustomerEntity customerEntity) {
        List<AddressEntity> getAddressEntityList = new ArrayList<>();
        List<CustomerAddressEntity> customerAddressEntityList = addressDao.getCustomerAddressByCustomer(customerEntity);
        if (customerAddressEntityList != null || !customerAddressEntityList.isEmpty()) {
            customerAddressEntityList.forEach(customerAddressEntity -> getAddressEntityList.add(customerAddressEntity.getAddress()));
        }
        return getAddressEntityList;
    }


    private boolean isValidPinCode(final String pincode) {
        Pattern digitPattern = Pattern.compile("\\d{6}");
        return digitPattern.matcher(pincode).matches();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public StateEntity getStateUuid(final String stateUuid) throws AddressNotFoundException {
        StateEntity getStateUuid = stateDao.getStateByUuid(stateUuid);
        if (getStateUuid == null) {
            throw new AddressNotFoundException("ANF-002", "No state by this id");
        }
        return getStateUuid;
    }
}

