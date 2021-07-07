package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryDao categoryDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public List<CategoryEntity> getCategoriesByRestaurant(final String restaurantUuid) {
        List<CategoryEntity> categoryEntities = categoryDao.getCategoriesByRestaurant(restaurantUuid);
        return categoryEntities;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<CategoryEntity> getAllCategories() {
        List<CategoryEntity> categoryEntityList = categoryDao.getAllCategoriesOrderByCategoryName();
        return categoryEntityList;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CategoryEntity getCategoryByUuid(final String categoryUuid) throws CategoryNotFoundException {
        if (categoryUuid == null) {
            throw new CategoryNotFoundException("CNF-001", "Category id field should not be empty");
        }
        CategoryEntity categoryEntity = categoryDao.getCategoryByUuid(categoryUuid);
        if (categoryEntity == null) {
            throw new CategoryNotFoundException("CNF-002", "No category by this id");
        }
        return categoryEntity;
    }
}

