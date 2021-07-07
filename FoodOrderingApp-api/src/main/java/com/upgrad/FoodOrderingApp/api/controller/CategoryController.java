package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.CategoriesListResponse;
import com.upgrad.FoodOrderingApp.api.model.CategoryDetailsResponse;
import com.upgrad.FoodOrderingApp.api.model.CategoryListResponse;
import com.upgrad.FoodOrderingApp.api.model.ItemList;
import com.upgrad.FoodOrderingApp.service.businness.CategoryService;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @CrossOrigin
    @RequestMapping(path = "category", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CategoriesListResponse> getAllCategories() {

        List<CategoryEntity> getAllCategories = categoryService.getAllCategories();
        List<CategoryListResponse> categoryListResponses = new ArrayList<>();

        if (getAllCategories.size() > 0) {
            for (CategoryEntity category : getAllCategories) {
                CategoryListResponse categoryListResponse = new CategoryListResponse();
                categoryListResponse.setId(UUID.fromString(category.getUuid()));
                categoryListResponse.setCategoryName(category.getCategoryName());
                categoryListResponses.add(categoryListResponse);
            }
        }

        CategoriesListResponse categoriesListResponse = new CategoriesListResponse();

        categoriesListResponse.setCategories(categoryListResponses);

        return new ResponseEntity<CategoriesListResponse>(categoriesListResponse, HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(path = "/category/{category_id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CategoryDetailsResponse> getCategoryById(@PathVariable("category_id") final String categoryUuid) throws CategoryNotFoundException {

        CategoryEntity categoryEntity = categoryService.getCategoryByUuid(categoryUuid);
        CategoryDetailsResponse categoryDetailsResponse = new CategoryDetailsResponse();
        categoryDetailsResponse.setId(UUID.fromString(categoryEntity.getUuid()));
        categoryDetailsResponse.setCategoryName(categoryEntity.getCategoryName());

        List<ItemEntity> itemEntityList = categoryEntity.getItems();
        List<ItemList> itemLists = new ArrayList<>();

        for (ItemEntity item : itemEntityList) {
            ItemList itemList = new ItemList();
            itemList.setId(UUID.fromString(item.getUuid()));
            itemList.setItemName(item.getItemName());
            itemList.setPrice(item.getPrice());
            if (item.getType().equals("0")) {
                itemList.setItemType(ItemList.ItemTypeEnum.valueOf("VEG"));
            } else {
                itemList.setItemType(ItemList.ItemTypeEnum.valueOf("NON_VEG"));
            }
            itemLists.add(itemList);
        }

        categoryDetailsResponse.setItemList(itemLists);

        return new ResponseEntity<CategoryDetailsResponse>(categoryDetailsResponse, HttpStatus.OK);
    }
}
