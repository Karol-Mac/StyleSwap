package com.restapi.styleswap.service;

import com.restapi.styleswap.payload.JwtAuthResponse;
import com.restapi.styleswap.payload.LoginDto;
import com.restapi.styleswap.payload.RegisterDto;

public interface AuthService {

    JwtAuthResponse login(LoginDto loginDto);

    String register(RegisterDto registerDto);

}
