package com.restapi.vinted.repository;

import com.restapi.vinted.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByPaymentIntentId(String paymentIntentId);
    Optional<Order> findByClotheId(long clotheId);
}