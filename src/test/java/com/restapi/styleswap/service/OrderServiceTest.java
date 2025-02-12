package com.restapi.styleswap.service;

import com.restapi.styleswap.entity.Clothe;
import com.restapi.styleswap.entity.Order;
import com.restapi.styleswap.entity.User;
import com.restapi.styleswap.exception.ResourceNotFoundException;
import com.restapi.styleswap.repository.OrderRepository;
import com.restapi.styleswap.service.impl.OrderServiceImpl;
import com.restapi.styleswap.utils.ClotheUtils;
import com.restapi.styleswap.utils.StripeManager;
import com.restapi.styleswap.utils.UserUtils;
import com.stripe.exception.ApiException;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ClotheUtils clotheUtils;

    @Mock
    private UserUtils userUtils;

    @Mock
    private StripeManager stripeManager;

    @Mock
    private Clothe clothe;

    @Mock
    private User buyer;

    @Mock
    private PaymentIntent paymentIntent;

    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createOrder_createsOrderSuccessfully() throws StripeException {

        when(clotheUtils.getClotheFromDB(1L)).thenReturn(clothe);
        when(userUtils.getUser("user@example.com")).thenReturn(buyer);
        when(clothe.getPrice()).thenReturn(BigDecimal.valueOf(1000.0));
        when(buyer.getStripeAccountId()).thenReturn("account_id");
        when(stripeManager.createPaymentIntent(1000L, "account_id")).thenReturn(paymentIntent);
        when(paymentIntent.getClientSecret()).thenReturn("client_secret");

        String result = orderService.createOrder(1L, "user@example.com");

        assertEquals("client_secret", result);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void createOrder_throwsResourceNotFoundExceptionWhenClotheNotFound() {
        when(clotheUtils.getClotheFromDB(1L)).thenThrow(new ResourceNotFoundException("Clothe", "id", 1L));

        assertThrows(ResourceNotFoundException.class, () -> orderService.createOrder(1L, "user@example.com"));
    }

    @Test
    void createOrder_throwsResourceNotFoundExceptionWhenUserNotFound() {
        when(clotheUtils.getClotheFromDB(1L)).thenReturn(clothe);
        when(userUtils.getUser("user@example.com"))
                .thenThrow(new ResourceNotFoundException("User", "email", "user@example.com"));

        assertThrows(ResourceNotFoundException.class, () -> orderService.createOrder(1L, "user@example.com"));
    }

    @Test
    void createOrder_throwsStripeException() throws StripeException {

        when(clotheUtils.getClotheFromDB(1L)).thenReturn(clothe);
        when(userUtils.getUser("user@example.com")).thenReturn(buyer);
        when(clothe.getPrice()).thenReturn(BigDecimal.valueOf(1000.0));
        when(buyer.getStripeAccountId()).thenReturn("account_id");
        when(stripeManager.createPaymentIntent(1000L, "account_id")).thenThrow(new ApiException("Stripe error", "" , "", 0, null));

        assertThrows(StripeException.class, () -> orderService.createOrder(1L, "user@example.com"));
    }
}
