package com.restapi.vinted.service.impl;

import com.restapi.vinted.entity.Role;
import com.restapi.vinted.entity.User;
import com.restapi.vinted.exception.ApiException;
import com.restapi.vinted.payload.JwtAuthResponse;
import com.restapi.vinted.payload.LoginDto;
import com.restapi.vinted.payload.RegisterDto;
import com.restapi.vinted.repository.RoleRepository;
import com.restapi.vinted.repository.UserRepository;
import com.restapi.vinted.security.JwtTokenProvider;
import com.restapi.vinted.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AuthServiceimpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    private final JwtTokenProvider jwtTokenProvider;

    public AuthServiceimpl(AuthenticationManager authenticationManager, UserRepository userRepository,
               RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public JwtAuthResponse login(LoginDto loginDto){
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginDto.getUsernameOrEmail(), loginDto.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            //returning generated token (when user is authenticated)
            JwtAuthResponse authResponse = new JwtAuthResponse();
            authResponse.setAccessToken(jwtTokenProvider.generateToken(authentication));

            return authResponse;
        } catch (BadCredentialsException exception){
            throw new ApiException(HttpStatus.FORBIDDEN, "Wrong username/email or password");
        }
    }

    @Override
    public String register(RegisterDto registerDto) {

        //check if username and email already exist in DB
        if(userRepository.existsByUsername(registerDto.getUsername()))
            throw new ApiException(HttpStatus.BAD_REQUEST, "Username is already taken");
        if(userRepository.existsByEmail(registerDto.getEmail()))
            throw new ApiException(HttpStatus.BAD_REQUEST, "Email already exist");

        User user = new User();
        user.setEmail(registerDto.getEmail());

        //password saved in DB has to be encoded
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setName(registerDto.getName());
        user.setUsername(registerDto.getUsername());

        //getting from DB default role for new user - ROLE_USER
        Role userRole = roleRepository.findByName("ROLE_USER").get();
        user.setRoles(Set.of(userRole));

        //saving new user in DB
        userRepository.save(user);

        return "User sign up successfully";
    }
}
