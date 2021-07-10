package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.common.Utility;
import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.*;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private ItemService itemService;

    @CrossOrigin
    @RequestMapping(path = "order/coupon/{coupon_name}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CouponDetailsResponse> GetCouponByCouponName(@PathVariable("coupon_name") final String couponName, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, CouponNotFoundException {

        final String accessToken = Utility.getAccessTokenFromAuthorization(authorization);
        CustomerEntity customerEntity = customerService.getCustomer(accessToken);
        final CouponEntity couponEntity = couponService.getCouponByCouponName(couponName);

        CouponDetailsResponse couponDetailsResponse = new CouponDetailsResponse();
        couponDetailsResponse.setId(UUID.fromString(couponEntity.getUuid()));
        couponDetailsResponse.setCouponName(couponEntity.getCouponName());
        couponDetailsResponse.setPercent(couponEntity.getPercent());

        return new ResponseEntity<CouponDetailsResponse>(couponDetailsResponse, HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(path = "order", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CustomerOrderResponse> getPastOrdersByCustomer(@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {

        final String accessToken = Utility.getAccessTokenFromAuthorization(authorization);
        CustomerEntity customerEntity = customerService.getCustomer(accessToken);
        List<OrderEntity> orderEntityList = orderService.getOrdersByCustomer(customerEntity.getUuid());

        List<OrderList> orders = new ArrayList<>();

        for (OrderEntity orderEntity : orderEntityList) {
            OrderList order = new OrderList();
            order.setId(UUID.fromString(orderEntity.getUuid()));
            order.setDate(orderEntity.getDate().toString());
            order.setBill(new BigDecimal(orderEntity.getBill()));
            order.setDiscount(new BigDecimal(orderEntity.getDiscount()));
            order.setCustomer(getCustomerOrdersList(orderEntity.getCustomer()));
            order.setCoupon(getOrderCouponList(orderEntity.getCoupon()));
            order.setAddress(getOrderAddressList(orderEntity.getAddress()));
            order.setPayment(getOrderPaymentList(orderEntity.getPayment()));
            List<OrderItemEntity> orderItems = orderEntity.getOrderItems();
            order.setItemQuantities(getItemQuantityResponseList(orderItems));
            orders.add(order);
        }
        CustomerOrderResponse customerOrderResponse = new CustomerOrderResponse();
        customerOrderResponse.setOrders(orders);
        return new ResponseEntity<CustomerOrderResponse>(customerOrderResponse, HttpStatus.OK);
    }


    @CrossOrigin
    @RequestMapping(path = "order", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SaveOrderResponse> saveOrder(@RequestBody SaveOrderRequest saveOrderRequest, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, CouponNotFoundException, AddressNotFoundException, PaymentMethodNotFoundException, RestaurantNotFoundException, ItemNotFoundException {

        final String accessToken = Utility.getAccessTokenFromAuthorization(authorization);
        CustomerEntity customerEntity = customerService.getCustomer(accessToken);

        CouponEntity couponEntity = couponService.getCouponByUuid(saveOrderRequest.getCouponId().toString());

        AddressEntity addressEntity = addressService.getAddressByUUID(saveOrderRequest.getAddressId(), customerEntity);

        PaymentEntity paymentEntity = paymentService.getPaymentByUuid(saveOrderRequest.getPaymentId().toString());

        RestaurantEntity restaurantEntity = restaurantService.restaurantByUuid(saveOrderRequest.getRestaurantId().toString());

        List<OrderItemEntity> orderItemEntities = new ArrayList<>();

        List<ItemQuantity> itemQuantityList = saveOrderRequest.getItemQuantities();

        for (ItemQuantity itemQuantity : itemQuantityList) {
            ItemEntity itemEntity = itemService.getItemByUuid(itemQuantity.getItemId().toString());
            OrderItemEntity orderItem = new OrderItemEntity();
            orderItem.setItem(itemEntity);
            orderItem.setPrice(itemQuantity.getPrice());
            orderItem.setQuantity(itemQuantity.getQuantity());
            orderItemEntities.add(orderItem);
        }

        OrderEntity order = new OrderEntity();
        order.setUuid(UUID.randomUUID().toString());
        order.setBill(saveOrderRequest.getBill().doubleValue());
        order.setDiscount(saveOrderRequest.getDiscount().doubleValue());
        order.setDate(ZonedDateTime.now());
        order.setAddress(addressEntity);
        order.setCoupon(couponEntity);
        order.setPayment(paymentEntity);
        order.setRestaurant(restaurantEntity);
        order.setCustomer(customerEntity);
        order.setOrderItems(Collections.emptyList());
        order = orderService.saveOrder(order);
        if (order != null) {
            for (OrderItemEntity orderItemEntity : orderItemEntities) {
                orderItemEntity.setOrder(order);
                orderService.saveOrderItem(orderItemEntity);
            }
        }
        SaveOrderResponse saveOrderResponse = new SaveOrderResponse();
        saveOrderResponse.setId(order.getUuid().toString());
        saveOrderResponse.setStatus("ORDER SUCCESSFULLY PLACED");

        return new ResponseEntity<SaveOrderResponse>(saveOrderResponse, HttpStatus.CREATED);
    }


    private OrderListCustomer getCustomerOrdersList(CustomerEntity customer) {
        OrderListCustomer orderListCustomer = new OrderListCustomer();
        orderListCustomer.setId(UUID.fromString(customer.getUuid()));
        orderListCustomer.setFirstName(customer.getFirstName());
        orderListCustomer.setLastName(customer.getLastName());
        orderListCustomer.setEmailAddress(customer.getEmail());
        orderListCustomer.setContactNumber(customer.getContactNumber());
        return orderListCustomer;
    }

    private OrderListCoupon getOrderCouponList(CouponEntity coupon) {
        OrderListCoupon orderListCoupon = new OrderListCoupon();
        orderListCoupon.setId(UUID.fromString(coupon.getUuid()));
        orderListCoupon.setCouponName(coupon.getCouponName());
        orderListCoupon.setPercent(coupon.getPercent());
        return orderListCoupon;
    }

    private OrderListPayment getOrderPaymentList(PaymentEntity payment) {
        OrderListPayment orderListPayment = new OrderListPayment();
        orderListPayment.setId(UUID.fromString(payment.getUuid()));
        orderListPayment.setPaymentName(payment.getPaymentName());
        return orderListPayment;
    }

    private OrderListAddress getOrderAddressList(AddressEntity address) {
        OrderListAddress orderListAddress = new OrderListAddress();
        orderListAddress.setId(UUID.fromString(address.getUuid()));
        orderListAddress.setFlatBuildingName(address.getFlatBuilNo());
        orderListAddress.setLocality(address.getLocality());
        orderListAddress.setCity(address.getCity());
        orderListAddress.setPincode(address.getPincode());
        OrderListAddressState orderListAddressState = new OrderListAddressState();
        orderListAddressState.setId(UUID.fromString(address.getState().getUuid()));
        orderListAddressState.setStateName(address.getState().getStateName());
        orderListAddress.setState(orderListAddressState);
        return orderListAddress;
    }

    private List<ItemQuantityResponse> getItemQuantityResponseList(List<OrderItemEntity> items) {
        List<ItemQuantityResponse> responseList = new ArrayList<>();

        for (OrderItemEntity orderItem : items) {
            ItemQuantityResponse response = new ItemQuantityResponse();

            ItemQuantityResponseItem responseItem = new ItemQuantityResponseItem();
            responseItem.setId(UUID.fromString(orderItem.getItem().getUuid()));
            responseItem.setItemName(orderItem.getItem().getItemName());
            responseItem.setItemPrice(orderItem.getItem().getPrice());
            ItemQuantityResponseItem.TypeEnum itemType =
                    Integer.valueOf(orderItem.getItem().getType()) == 0
                            ? ItemQuantityResponseItem.TypeEnum.VEG
                            : ItemQuantityResponseItem.TypeEnum.NON_VEG;
            responseItem.setType(itemType);
            response.setItem(responseItem);

            response.setQuantity(orderItem.getQuantity());
            response.setPrice(orderItem.getPrice());
            responseList.add(response);
        }
        return responseList;
    }
}