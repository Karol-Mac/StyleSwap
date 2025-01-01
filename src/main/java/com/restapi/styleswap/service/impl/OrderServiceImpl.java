package com.restapi.styleswap.service.impl;

import com.restapi.styleswap.entity.Clothe;
import com.restapi.styleswap.entity.Order;
import com.restapi.styleswap.entity.User;
import com.restapi.styleswap.exception.ApiException;
import com.restapi.styleswap.repository.OrderRepository;
import com.restapi.styleswap.service.OrderService;
import com.restapi.styleswap.utils.ClotheUtils;
import com.restapi.styleswap.utils.OrderStatus;
import com.restapi.styleswap.utils.StripeManager;
import com.restapi.styleswap.utils.UserUtils;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ClotheUtils clotheUtils;
    private final UserUtils userUtils;


    public OrderServiceImpl(OrderRepository orderRepository, ClotheUtils clotheUtils, UserUtils userUtils) {
        this.orderRepository = orderRepository;
        this.clotheUtils = clotheUtils;
        this.userUtils = userUtils;
    }

    @Override
    public String createOrder(long clotheId, String email) throws StripeException {

        var clothe = clotheUtils.getClotheFromDB(clotheId);
        var buyer = userUtils.getUser(email);
        var paymentIntent = StripeManager.createPaymentIntent(
                clothe.getPrice().longValue(),
                buyer.getStripeAccountId());

        saveOrderEntity(clothe, buyer, paymentIntent);

        return paymentIntent.getClientSecret();
    }

    //FIXME: jak na razie brak opcji cancelowania zamówienia - poszło i tyle XD
//    @Override
//    public void cancelOrder(long orderId, String userEmail) {
//        var order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new RuntimeException("Order not found"));
//
//        checkIfOrderIsValid(userEmail, order);
//
//        order.setOrderStatus(OrderStatus.CANCELLED);
//        orderRepository.save(order);
//    }

    private void saveOrderEntity(Clothe clothe, User buyer, PaymentIntent paymentIntent) {
        var order =  Order.builder()
                .clothe(clothe)
                .buyer(buyer)
                .orderStatus(OrderStatus.PENDING)
                .seller(clothe.getUser())
                .totalAmount(clothe.getPrice())
                .paymentIntentId(paymentIntent.getId())
                .build();
        orderRepository.save(order);
    }

    private static void checkIfOrderIsValid(String name, Order order) {
        if (!order.getBuyer().getEmail().equals(name))
            throw new RuntimeException("You are not allowed to cancel this order");
        else if (order.getOrderStatus() == OrderStatus.PAID)
            throw new ApiException( HttpStatus.CONFLICT, "This ordder is already paid, you can't cancel it");
    }
}