package com.upgrad.FoodOrderingApp.service.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table(name = "item")
@NamedNativeQueries({@NamedNativeQuery(
        name = "topFivePopularItemsByRestaurant",
        query = "select * from item where id in "
                + "(select item_id from order_item where order_id in "
                + "(select id from orders where restaurant_id = ? ) "
                + "group by order_item.item_id "
                + "order by (count(order_item.order_id)) "
                + "desc LIMIT 5)",
        resultClass = ItemEntity.class)
})
@NamedQueries({
        @NamedQuery(name = "itemByUUID", query = "select i from ItemEntity i where i.uuid=:itemUUID"),
        @NamedQuery(
                name = "getAllItemsByCategoryAndRestaurant",
                query =
                        "select i from ItemEntity i  where i.id in (select ri.itemId from RestaurantItemEntity ri "
                                + "inner join CategoryItemEntity ci on ri.itemId = ci.itemId "
                                + "where ri.restaurantId = (select r.id from RestaurantEntity r where "
                                + "r.uuid=:restaurantUuid) and ci.categoryId = "
                                + "(select c.id from CategoryEntity c where c.uuid=:categoryUuid ) )"
                                + "order by i.itemName asc")
})
public class ItemEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "uuid")
    @NotNull
    @Size(max = 200)
    private String uuid;

    @Column(name = "item_name")
    @Size(max = 30)
    private String itemName;

    @Column(name = "price")
    private Integer price;

    @Column(name = "type")
    private String type;

    public ItemEntity() {
    }

    public ItemEntity(String uuid, String itemName, Integer price, String type) {
        this.uuid = uuid;
        this.itemName = itemName;
        this.price = price;
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object obj) {
        return new EqualsBuilder().append(this, obj).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this).hashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
