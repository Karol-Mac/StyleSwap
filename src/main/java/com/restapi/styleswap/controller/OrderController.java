package com.restapi.styleswap.controller;

import com.restapi.styleswap.exception.ApiException;
import com.restapi.styleswap.service.OrderService;
import com.restapi.styleswap.service.WebhookHandler;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
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

//    @PatchMapping("/{orderId}/cancel")
//    public ResponseEntity<Void> cancelOrder(@PathVariable long orderId, Principal principal) {
//        orderService.cancelOrder(orderId, principal.getName());
//
//        return ResponseEntity.ok().build();
//    }

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


//    @PostMapping("/webhook")
//    public ResponseEntity<String> handleStripeEvent(
//            @RequestBody String payload,
//            @RequestHeader("Stripe-Signature") String sigHeader)
//            throws StripeException {
//
//            Event event = Webhook.constructEvent(payload, sigHeader, secret);
//
//            String eventType = event.getType();
//            PaymentIntent paymentIntent = null;
//
//            log.error("Event: {}", event.getType());
//
//            if ("account.updated".equals(event.getType())) {
//                Account account = (Account) event.getDataObjectDeserializer().getObject().get();
//                // Zaktualizuj status konta w bazie
//                log.info("Account updated: {}", account.getId());
//
//                return ResponseEntity.ok("Webhook received XD");
//            }
//
//            if (event.getDataObjectDeserializer().getObject().isPresent()) {
//                paymentIntent = (PaymentIntent) event.getData().getObject();
//            }
//
//            if (paymentIntent != null) {
//                switch (eventType) {
//                    case "payment_intent.succeeded" -> {
//                        log.info("Payment succeeded for amount: {}", paymentIntent.getAmount());
//                        log.info("Payment succeeded for account: {}", paymentIntent.getTransferData().getDestination());
//                        // Dodaj logikę np. zapis do bazy danych
//                    }
//
//                    case "payment_intent.payment_failed" ->
//                            log.warn("Payment failed for amount: {}", paymentIntent.getAmount());
//                    // Dodaj logikę np. powiadomienie użytkownika
//
//                    case "payment_intent.canceled" ->
//                            log.warn("Payment was canceled for amount: {}", paymentIntent.getAmount());
//                    // Dodaj logikę np. aktualizacja zamówienia
//
//                    default -> log.warn("Unhandled event type: {}", eventType);
//                }
//            }
//
//            return ResponseEntity.ok("Webhook received");
////        } catch (Exception e) {
////            log.error("Error processing webhook: {}", e.getMessage());
////            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Webhook error");
////        }
//    }

    //TODO: rest of the endpoint's should be in AdminController class
    //      e.g. getAllOrder, getOrderById
}