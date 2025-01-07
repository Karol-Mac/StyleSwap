package com.restapi.styleswap.controller;

import com.restapi.styleswap.exception.ErrorDetails;
import com.restapi.styleswap.exception.ApiException;
import com.restapi.styleswap.payload.JwtAuthResponse;
import com.restapi.styleswap.payload.LoginDto;
import com.restapi.styleswap.payload.RegisterDto;
import com.restapi.styleswap.service.AuthService;
import com.restapi.styleswap.utils.Constant;
import com.stripe.exception.StripeException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "AuthController", description = "Authentication operations - login, register")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @Operation(summary = "Log in a user", description = "Authenticates a user and returns an access token.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User authenticated successfully",
                    content = @Content(schema = @Schema(implementation = JwtAuthResponse.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "email must not be null",
                    content = @Content(schema = @Schema(implementation = String[].class))),
            @ApiResponse(
                    responseCode = "403",
                    description = "Invalid login data",
                    content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
    })
    @PostMapping(value = {"/login", "/signin"})
    public ResponseEntity<JwtAuthResponse> login(@RequestBody @Valid LoginDto loginDto){

        return ResponseEntity.ok(authService.login(loginDto));
    }

    @Operation(summary = "Register a user", description = "Creates new user, and returns a success message.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User authenticated successfully",
                    content = @Content(schema = @Schema(implementation = JwtAuthResponse.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "username: " + Constant.USERNAME_VALIDATION_FAILED,
                    content = @Content(schema = @Schema(implementation = String[].class))),
            @ApiResponse(
                    responseCode = "409",
                    description = "Email/Username already exists",
                    content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
    })
    @PostMapping(value = {"/register", "/signup"})
    public ResponseEntity<String> register(@RequestBody @Valid RegisterDto registerDto) throws StripeException {

        String phoneNumber = registerDto.getPhoneNumber().replaceAll("[^0-9]", "");
        registerDto.setPhoneNumber(phoneNumber);

        return new ResponseEntity<>(authService.register(registerDto), HttpStatus.CREATED);
    }
}
