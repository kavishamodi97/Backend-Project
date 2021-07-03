package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class CustomerDao {
    @PersistenceContext
    private EntityManager entityManager;

    public CustomerEntity signupCustomer(CustomerEntity customer) {
        entityManager.persist(customer);
        return customer;
    }

    public CustomerEntity getCustomerByContactNumber(final String contactNumber) {
        try {
            return entityManager.createNamedQuery("customerByContactNumber", CustomerEntity.class)
                    .setParameter("contact_number", contactNumber).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public CustomerEntity updateCustomer(CustomerEntity customerEntity) {
        entityManager.merge(customerEntity);
        return customerEntity;
    }
}
