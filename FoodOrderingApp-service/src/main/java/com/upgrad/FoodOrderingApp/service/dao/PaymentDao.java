package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class PaymentDao {

    @PersistenceContext
    private EntityManager entityManager;

    public List<PaymentEntity> getPaymentMethods() {
        return entityManager.createNamedQuery("getAllPaymentMethods", PaymentEntity.class).getResultList();
    }

    public PaymentEntity getPaymentUuid(final String paymentUuid) {
        return entityManager.createNamedQuery("getPaymentUuid", PaymentEntity.class).setParameter("paymentUuid", paymentUuid).getSingleResult();
    }
}
