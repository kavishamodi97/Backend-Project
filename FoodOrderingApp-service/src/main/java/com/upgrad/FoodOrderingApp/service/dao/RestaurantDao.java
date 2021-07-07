package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class RestaurantDao {

    @PersistenceContext
    private EntityManager entityManager;

    public List<RestaurantEntity> restaurantsByRating() {
        return entityManager.createNamedQuery("restaurantsByRating", RestaurantEntity.class).getResultList();
    }

    public List<RestaurantEntity> getRestaurantsByName(final String restaurantName) {
        return entityManager.createNamedQuery("getRestaurantsByName", RestaurantEntity.class).setParameter("restaurantName", "%" + restaurantName + "%").getResultList();
    }

    public List<RestaurantEntity> getRestaurantsByCategoryUuid(final String categoryUuid) {
        return entityManager.createNamedQuery("getRestaurantsByCategoryUuid", RestaurantEntity.class).setParameter("categoryUuid", categoryUuid).getResultList();
    }

    public RestaurantEntity getRestaurantByUuid(final String restaurantUuid) {
        try {
            return entityManager.createNamedQuery("getRestaurantByRestaurantUuid", RestaurantEntity.class).setParameter("restaurantUuid", restaurantUuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
}
