package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class CategoryDao {

    @PersistenceContext
    private EntityManager entityManager;

    public List<CategoryEntity> getCategoriesByRestaurant(final String restaurantUuid) {
        try {
            return entityManager.createNamedQuery("getCategoriesByRestaurant", CategoryEntity.class).setParameter("restaurantUuid", restaurantUuid).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<CategoryEntity> getAllCategoriesOrderByCategoryName() {
        return entityManager.createNamedQuery("getAllCategoriesOrderByCategoryName", CategoryEntity.class).getResultList();
    }

    public CategoryEntity getCategoryByUuid(final String categoryUuid) {
        try {
            return entityManager.createNamedQuery("getCategoryByUuid", CategoryEntity.class).setParameter("categoryUuid", categoryUuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
}
