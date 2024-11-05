package com.restapi.vinted.service;

public interface OrderService {

    void createOrder(long clotheId, String email);

    void cancelOrder(long orderId, String name);
}
