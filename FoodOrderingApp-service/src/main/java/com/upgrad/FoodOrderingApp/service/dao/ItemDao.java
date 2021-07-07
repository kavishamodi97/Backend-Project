package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.List;

@Repository
public class ItemDao {

    @PersistenceContext
    private EntityManager entityManager;

    public List<ItemEntity> getItemsByCategoryAndRestaurant(final String restaurantUuid, final String categoryUuid) {
        return entityManager.createNamedQuery("getAllItemsByCategoryAndRestaurant").setParameter("restaurantUuid", restaurantUuid).setParameter("categoryUuid", categoryUuid).getResultList();
    }

    public List<ItemEntity> getTopFiveItems(final RestaurantEntity restaurantEntity) {
        List<ItemEntity> items = entityManager.createNamedQuery("topFivePopularItemsByRestaurant", ItemEntity.class).setParameter(0, restaurantEntity.getId()).getResultList();
        if (items != null) {
            return items;
        }
        return Collections.emptyList();
    }
}
