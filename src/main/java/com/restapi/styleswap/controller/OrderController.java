package com.restapi.styleswap.controller;

import com.restapi.styleswap.service.OrderService;
import com.stripe.exception.StripeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService){
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<String> createOrder(long clotheId, Principal principal) throws StripeException {
        var clientSercet = orderService.createOrder(clotheId, principal.getName());
        return new ResponseEntity<>(clientSercet, HttpStatus.CREATED);
    }

//    @PatchMapping("/{orderId}/cancel")
//    public ResponseEntity<Void> cancelOrder(@PathVariable long orderId, Principal principal) {
//        orderService.cancelOrder(orderId, principal.getName());
//
//        return ResponseEntity.ok().build();
//    }

    //TODO: add webhook endpoint - message the seller about payment

    //TODO: rest of the endpoint's should be in AdminController class
    //      e.g. getAllOrder, getOrderById
}
