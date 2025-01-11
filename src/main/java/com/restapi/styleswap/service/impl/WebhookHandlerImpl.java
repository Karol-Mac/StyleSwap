package com.restapi.styleswap.service.impl;

import com.restapi.styleswap.entity.Clothe;
import com.restapi.styleswap.entity.Order;
import com.restapi.styleswap.entity.User;
import com.restapi.styleswap.exception.ResourceNotFoundException;
import com.restapi.styleswap.repository.ClotheRepository;
import com.restapi.styleswap.repository.OrderRepository;
import com.restapi.styleswap.repository.UserRepository;
import com.restapi.styleswap.service.WebhookHandler;
import com.restapi.styleswap.utils.OrderStatus;
import com.stripe.model.Account;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class WebhookHandlerImpl implements WebhookHandler {

    private static final Logger log = LoggerFactory.getLogger(WebhookHandlerImpl.class);
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ClotheRepository clotheRepository;

    public WebhookHandlerImpl(UserRepository userRepository, OrderRepository orderRepository,
                              ClotheRepository clotheRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.clotheRepository = clotheRepository;
    }

    @Override
    @Transactional
    public void handleAccountUpdated(Event event) {
        event.getDataObjectDeserializer().getObject().ifPresent(data -> {
            Account account = (Account) data;

            User user = userRepository.findByStripeAccountId(account.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "stripeAccountId", account.getId()));

            user.setStripeAccountCreated(true);
        });
    }

    @Override
    @Transactional
    public void handlePaymentIntent(Event event) {
        event.getDataObjectDeserializer().getObject().ifPresent(data -> {
            PaymentIntent paymentIntent = (PaymentIntent) data;

            switch (event.getType()) {
                case "payment_intent.succeeded" -> handleSucceedPaymentIntent(paymentIntent);
                case "payment_intent.payment_failed" -> handleFailedPaymentIntent(paymentIntent);
                case "payment_intent.canceled" -> handleCancelledPaymentIntent(paymentIntent);
            }
        });
    }

    @Transactional(propagation = Propagation.NESTED)
    void handleSucceedPaymentIntent(PaymentIntent paymentIntent) {
        Order order = orderRepository.findByPaymentIntentId(paymentIntent.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Order", "paymentIntentId", paymentIntent.getId()));

        order.setOrderStatus(OrderStatus.PAID);
//        order.setCompletedDate();
        log.warn("Data transakcji: {}", LocalDateTime.ofEpochSecond(paymentIntent.getLatestChargeObject().getCreated(), 0, ZoneOffset.UTC));
        Clothe clothe = clotheRepository.findById(order.getClothe().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Clothe", "id", order.getClothe().getId()));

        clothe.setAvailable(false);
    }

    @Transactional(propagation = Propagation.NESTED)
    void handleCancelledPaymentIntent(PaymentIntent paymentIntent) {

    }

    @Transactional(propagation = Propagation.NESTED)
    void handleFailedPaymentIntent(PaymentIntent paymentIntent) {

    }
}
