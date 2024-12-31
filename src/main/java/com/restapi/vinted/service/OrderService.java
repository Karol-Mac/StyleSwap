package com.restapi.vinted.service;

import com.stripe.exception.StripeException;

public interface OrderService {

    String createOrder(long clotheId, String email) throws StripeException;

//    void cancelOrder(long orderId, String name);
}
