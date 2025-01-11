package com.restapi.styleswap.service;

import com.stripe.model.Event;

public interface WebhookHandler {
    void handleAccountUpdated(Event event);

    void handlePaymentIntent(Event event);
}
