package com.restapi.styleswap.controller;

import com.restapi.styleswap.exception.ApiException;
import com.restapi.styleswap.service.OrderService;
import com.restapi.styleswap.service.WebhookHandler;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Value("${stripe.webhook.secret}")
    private String secret;

    private final OrderService orderService;
    private final WebhookHandler webhookHandler;

    public OrderController(OrderService orderService, WebhookHandler webhookHandler){
        this.orderService = orderService;
        this.webhookHandler = webhookHandler;
    }

    @PostMapping
    public ResponseEntity<String> order(@RequestParam long clotheId,
                                        Principal principal) throws StripeException {

        var clientSercet = orderService.createOrder(clotheId, principal.getName());
        return new ResponseEntity<>(clientSercet, HttpStatus.CREATED);
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeEvent(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) throws StripeException {

        Event event = Webhook.constructEvent(payload, sigHeader, secret);
        String eventType = event.getType();
        switchOverEventTypes(eventType, event);
        return ResponseEntity.ok("Webhook received");
    }

    private void switchOverEventTypes(String eventType, Event event) {
        switch (eventType) {
            case "account.updated" -> webhookHandler.handleAccountUpdated(event);
            case "payment_intent.succeeded",
                 "payment_intent.payment_failed",
                 "payment_intent.canceled" -> webhookHandler.handlePaymentIntent(event);
            default -> throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unhandled event type: " + eventType);
        }
    }

    //TODO: rest of the endpoint's should be in AdminController class
    //      e.g. getAllOrder, getOrderById
}