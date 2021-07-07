package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantDao restaurantDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public List<RestaurantEntity> restaurantsByRating() {
        return restaurantDao.restaurantsByRating();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<RestaurantEntity> restaurantsByName(final String restaurantName) throws RestaurantNotFoundException {

        if (restaurantName == null || restaurantName.isEmpty()) {
            throw new RestaurantNotFoundException("RNF-003", "Restaurant name field should not be empty");
        }
        List<RestaurantEntity> restaurantEntities = restaurantDao.getRestaurantsByName(restaurantName);
        return restaurantEntities;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<RestaurantEntity> restaurantsByCategory(final String categoryUuid) throws CategoryNotFoundException {

        if (categoryUuid == null) {
            throw new CategoryNotFoundException("CNF-001", "Category id field should not be empty");
        }
        List<RestaurantEntity> restaurantCategoryByUuid = restaurantDao.getRestaurantsByCategoryUuid(categoryUuid);

        if (restaurantCategoryByUuid == null) {
            throw new CategoryNotFoundException("CNF-002", "No category by this id");
        }
        return restaurantCategoryByUuid;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public RestaurantEntity restaurantByUuid(final String restaurantUuid) throws RestaurantNotFoundException {
        RestaurantEntity restaurant = restaurantDao.getRestaurantByUuid(restaurantUuid);
        if (restaurant == null) {
            throw new RestaurantNotFoundException("RNF-001", "No restaurant by this id");
        }
        return restaurant;
    }
}
