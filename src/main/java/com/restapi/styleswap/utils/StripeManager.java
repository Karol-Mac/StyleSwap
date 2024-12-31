package com.restapi.styleswap.utils;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

//@Component
public class StripeManager {

    public static PaymentIntent createPaymentIntent(Long amount, String account_Id) throws StripeException {
        PaymentIntentCreateParams params =
                PaymentIntentCreateParams.builder()
                        .setAmount(amount)
                        .setCurrency("pln")
                        .setAutomaticPaymentMethods(
                                PaymentIntentCreateParams
                                        .AutomaticPaymentMethods.builder().setEnabled(true).build()
                        )
                        .setTransferData(
                                PaymentIntentCreateParams.TransferData.builder()
                                        .setDestination(account_Id)
                                        .build()
                        )
                        .build();

        return PaymentIntent.create(params);
    }
}