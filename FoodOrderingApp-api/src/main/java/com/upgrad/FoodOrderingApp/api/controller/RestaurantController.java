package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.RestaurantDetailsResponseAddress;
import com.upgrad.FoodOrderingApp.api.model.RestaurantDetailsResponseAddressState;
import com.upgrad.FoodOrderingApp.api.model.RestaurantList;
import com.upgrad.FoodOrderingApp.api.model.RestaurantListResponse;
import com.upgrad.FoodOrderingApp.service.businness.CategoryService;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantService;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private CategoryService categoryService;

    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, path = "/restaurant", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getRestaurants() {

        List<RestaurantEntity> allRestaurants = restaurantService.restaurantsByRating();
        List<RestaurantList> allRestaurantsList = createRestaurantList(allRestaurants);
        RestaurantListResponse restaurantListResponse = new RestaurantListResponse().restaurants(allRestaurantsList);

        return new ResponseEntity<>(restaurantListResponse, HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, path = "restaurant/name/{restaurant_name}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getRestaurantsByName(@PathVariable("restaurant_name") final String restaurantName) throws RestaurantNotFoundException {

        List<RestaurantEntity> restaurantEntityByName = restaurantService.restaurantsByName(restaurantName);
        List<RestaurantList> restaurantList = createRestaurantList(restaurantEntityByName);
        RestaurantListResponse restaurantListResponse = new RestaurantListResponse().restaurants(restaurantList);

        if (restaurantList == null || restaurantList.isEmpty()) {
            return new ResponseEntity<RestaurantListResponse>(restaurantListResponse, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<RestaurantListResponse>(restaurantListResponse, HttpStatus.OK);
        }
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, path = "restaurant/category/{category_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getRestaurantsByCategoryUuid(@PathVariable("category_id") final String categoryUuid) throws CategoryNotFoundException {

        List<RestaurantEntity> restaurantsByCategoryEntity = restaurantService.restaurantsByCategory(categoryUuid);
        List<RestaurantList> restaurantCategoryList = createRestaurantList(restaurantsByCategoryEntity);

        RestaurantListResponse restaurantListResponse = new RestaurantListResponse().restaurants(restaurantCategoryList);

        if (restaurantCategoryList == null || restaurantCategoryList.isEmpty()) {
            return new ResponseEntity<RestaurantListResponse>(restaurantListResponse, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<RestaurantListResponse>(restaurantListResponse, HttpStatus.OK);
        }
    }

    private List<RestaurantList> createRestaurantList(final List<RestaurantEntity> allRestaurants) {
        List<RestaurantList> allRestaurantsList = new ArrayList<>();
        for (RestaurantEntity restaurantEntity : allRestaurants) {
            RestaurantList restaurantList = new RestaurantList();
            restaurantList.setId(UUID.fromString(restaurantEntity.getUuid()));
            RestaurantDetailsResponseAddress restaurantDetailsResponseAddress = createRestaurantDetailsResponseAddress(restaurantEntity.getAddress());

            restaurantList.setAddress(restaurantDetailsResponseAddress);
            restaurantList.setAveragePrice(restaurantEntity.getAveragePriceForTwo());

            List<CategoryEntity> categoryEntities = categoryService.getCategoriesByRestaurant(restaurantEntity.getUuid());
            StringBuilder categoriesString = new StringBuilder();
            for (CategoryEntity category : categoryEntities) {
                categoriesString.append(category.getCategoryName() + ", ");
            }

            restaurantList.setCategories(categoriesString.toString().replaceAll(", $", ""));

            restaurantList.setCustomerRating(BigDecimal.valueOf(restaurantEntity.getCustomerRating()));
            restaurantList.setNumberCustomersRated(restaurantEntity.getNumberOfCustomersRated());
            restaurantList.setPhotoURL(restaurantEntity.getPhotoUrl());
            restaurantList.setRestaurantName(restaurantEntity.getRestaurantName());
            allRestaurantsList.add(restaurantList);
        }

        return allRestaurantsList;
    }

    private RestaurantDetailsResponseAddress createRestaurantDetailsResponseAddress(AddressEntity restaurantAddress) {
        RestaurantDetailsResponseAddress restaurantDetailsResponseAddress = new RestaurantDetailsResponseAddress();
        RestaurantDetailsResponseAddressState restaurantDetailsResponseAddressState = new RestaurantDetailsResponseAddressState();
        AddressEntity addressEntity = restaurantAddress;
        restaurantDetailsResponseAddress.setId(UUID.fromString(addressEntity.getUuid()));
        restaurantDetailsResponseAddress.setFlatBuildingName(addressEntity.getFlatBuilNo());
        restaurantDetailsResponseAddress.setCity(addressEntity.getCity());
        restaurantDetailsResponseAddress.setLocality(addressEntity.getLocality());
        restaurantDetailsResponseAddress.setPincode(addressEntity.getPincode());
        restaurantDetailsResponseAddressState.setId(UUID.fromString(addressEntity.getState().getUuid()));
        restaurantDetailsResponseAddressState.setStateName(addressEntity.getState().getStateName());
        restaurantDetailsResponseAddress.setState(restaurantDetailsResponseAddressState);
        return restaurantDetailsResponseAddress;
    }
}
