package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.ItemDao;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.ItemNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ItemService {

    @Autowired
    private ItemDao itemDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public List<ItemEntity> getItemsByCategoryAndRestaurant(final String restaurantUuid, final String categoryUuid) {
        List<ItemEntity> itemEntityList = itemDao.getItemsByCategoryAndRestaurant(restaurantUuid, categoryUuid);
        return itemEntityList;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<ItemEntity> getTopFiveItems(final RestaurantEntity restaurantEntity) {
        List<ItemEntity> itemEntityList = itemDao.getTopFiveItemsOrdersByRestaurant(restaurantEntity);
        return itemEntityList;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public ItemEntity getItemByUuid(final String itemUuid) throws ItemNotFoundException {

        ItemEntity item = itemDao.getItemUuid(itemUuid);

        if (item == null) {
            throw new ItemNotFoundException("INF-003", "No item by this id exist");
        }
        return item;
    }

}
