package com.restapi.styleswap.controller;

import com.restapi.styleswap.payload.JwtAuthResponse;
import com.restapi.styleswap.payload.LoginDto;
import com.restapi.styleswap.payload.RegisterDto;
import com.restapi.styleswap.service.AuthService;
import com.stripe.exception.StripeException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(value = {"/login", "/signin"})
    public ResponseEntity<JwtAuthResponse> login(@RequestBody @Valid LoginDto loginDto){

        return ResponseEntity.ok(authService.login(loginDto));
    }


    @PostMapping(value = {"/register", "/signup"})
    public ResponseEntity<String> register(@RequestBody @Valid RegisterDto registerDto) throws StripeException {

        String phoneNumber = registerDto.getPhoneNumber().replaceAll("[^0-9]", "");
        registerDto.setPhoneNumber(phoneNumber);

        return new ResponseEntity<>(authService.register(registerDto), HttpStatus.CREATED);
    }
}
