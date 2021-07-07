package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.ItemList;
import com.upgrad.FoodOrderingApp.api.model.ItemListResponse;
import com.upgrad.FoodOrderingApp.service.businness.ItemService;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantService;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class ItemController {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private ItemService itemService;

    @CrossOrigin
    @RequestMapping(path = "item/restaurant/{restaurant_id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ItemListResponse> getTopFiveItemsByPopularity(@PathVariable("restaurant_id") final String restaurantUuid) throws RestaurantNotFoundException {

        RestaurantEntity restaurantEntity = restaurantService.restaurantByUuid(restaurantUuid);
        List<ItemEntity> getTopFiveItems = itemService.getTopFiveItems(restaurantEntity);
        ItemListResponse itemListResponse = new ItemListResponse();
        for (ItemEntity items : getTopFiveItems) {
            ItemList itemList = new ItemList();
            itemList.setId(UUID.fromString(items.getUuid()));
            itemList.setItemName(items.getItemName());
            itemList.setPrice(items.getPrice());
            ItemList.ItemTypeEnum itemTypeEnum = (Integer.valueOf(items.getType()) == 0)
                    ? ItemList.ItemTypeEnum.VEG
                    : ItemList.ItemTypeEnum.NON_VEG;
            itemList.setItemType(itemTypeEnum);
            itemListResponse.add(itemList);
        }
        return new ResponseEntity<ItemListResponse>(itemListResponse, HttpStatus.OK);
    }
}
