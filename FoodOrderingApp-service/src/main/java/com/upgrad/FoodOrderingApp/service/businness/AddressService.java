package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddressService {

//    @Transactional(propagation = Propagation.REQUIRED)
//    public AddressEntity saveAddress(AddressEntity addressEntity, final String accessToken) throws SaveAddressException {
//
//        if (!isValidPincode(addressEntity.getPincode())) {
//            throw new SaveAddressException("SAR-002", "Invalid pincode");
//        }
//        return null;
//    }
}

