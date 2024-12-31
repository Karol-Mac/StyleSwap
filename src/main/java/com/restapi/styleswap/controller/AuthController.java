package com.restapi.styleswap.controller;

import com.restapi.styleswap.exception.ApiException;
import com.restapi.styleswap.payload.JwtAuthResponse;
import com.restapi.styleswap.payload.LoginDto;
import com.restapi.styleswap.payload.RegisterDto;
import com.restapi.styleswap.service.AuthService;
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
    public ResponseEntity<JwtAuthResponse> login(
            @RequestBody(required = false) @Valid LoginDto loginDto){

        if(loginDto == null)
            throw new ApiException(HttpStatus.BAD_REQUEST, "Request body is missing");

        return ResponseEntity.ok(authService.login(loginDto));
    }


    @PostMapping(value = {"/register", "/signup"})
    public ResponseEntity<String> register(
                    @RequestBody(required = false) @Valid RegisterDto registerDto){

        if(registerDto == null)
                    throw new ApiException(HttpStatus.BAD_REQUEST, "Request body is missing");

        return new ResponseEntity<>(authService.register(registerDto), HttpStatus.CREATED);
    }
}
