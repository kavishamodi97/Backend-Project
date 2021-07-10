package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.OrderEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.List;

@Repository
public class OrderDao {

    @PersistenceContext
    private EntityManager entityManager;

    public List<OrderEntity> getOrdersByCustomers(final String customerUuid) {
        List<OrderEntity> ordersByCustomer = entityManager.createNamedQuery("getOrdersByCustomer", OrderEntity.class).setParameter("customerUuid", customerUuid).getResultList();
        if (ordersByCustomer != null) {
            return ordersByCustomer;
        }
        return Collections.emptyList();
    }

    public OrderEntity saveOrder(final OrderEntity orderEntity) {
        entityManager.persist(orderEntity);
        return orderEntity;
    }

    public OrderItemEntity saveOrderItem(final OrderItemEntity orderItemEntity) {
        entityManager.persist(orderItemEntity);
        return orderItemEntity;
    }
}
