package com.restapi.styleswap.utils;

import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import com.stripe.model.PaymentIntent;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.AccountLinkCreateParams;
import com.stripe.param.PaymentIntentCreateParams;

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

    public static String generateStripeRegisterLink(Account account) throws StripeException {
        AccountLinkCreateParams linkParams = AccountLinkCreateParams.builder()
                .setAccount(account.getId())
                .setType(AccountLinkCreateParams.Type.ACCOUNT_ONBOARDING)
                .setReturnUrl("http://localhost:8080/api/clothes/my")
                .setRefreshUrl("http://localhost:8080/api/clothes/my")      //FIXME: BAD PRACTICE
                .build();

        return AccountLink.create(linkParams).getUrl();
    }

    public static Account createStripeAccount(String email) throws StripeException {
        AccountCreateParams accountParams = AccountCreateParams.builder()
                .setType(AccountCreateParams.Type.EXPRESS)
                .setCountry("PL")
                .setEmail(email)
                .build();
        return Account.create(accountParams);
    }

}