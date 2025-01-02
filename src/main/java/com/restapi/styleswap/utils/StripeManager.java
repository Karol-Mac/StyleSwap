package com.restapi.styleswap.utils;

import com.restapi.styleswap.payload.RegisterDto;
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

    public static Account createStripeAccount(RegisterDto registerDto) throws StripeException {
        AccountCreateParams.BusinessProfile profile = createBusinessProfile();
        AccountCreateParams.Individual individual = createIndividual(registerDto);
        AccountCreateParams accountParams = getAccountCreateParams(registerDto, individual, profile);

        return Account.create(accountParams);
    }

    private static AccountCreateParams getAccountCreateParams(
            RegisterDto registerDto,
            AccountCreateParams.Individual individual,
            AccountCreateParams.BusinessProfile profile) {
        return AccountCreateParams.builder()
                .setType(AccountCreateParams.Type.EXPRESS)
                .setCountry("PL")
                .setEmail(registerDto.getEmail())
                .setIndividual(individual)
                .setBusinessProfile(profile)
                .setBusinessType(AccountCreateParams.BusinessType.INDIVIDUAL)
                .build();
    }

    private static AccountCreateParams.BusinessProfile createBusinessProfile() {
        return AccountCreateParams.BusinessProfile.builder()
                .setProductDescription("Clothes selling user")
                .setMcc("7296")
                .build();
    }

    private static AccountCreateParams.Individual createIndividual(RegisterDto registerDto) {
        return AccountCreateParams.Individual.builder()
                .setFirstName(registerDto.getFirstName())
                .setLastName(registerDto.getLastName())
                .setPhone("+48"+registerDto.getPhoneNumber())
                .build();
    }
}