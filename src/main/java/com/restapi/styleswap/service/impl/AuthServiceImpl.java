package com.restapi.styleswap.service.impl;

import com.restapi.styleswap.entity.Role;
import com.restapi.styleswap.entity.User;
import com.restapi.styleswap.payload.JwtAuthResponse;
import com.restapi.styleswap.payload.LoginDto;
import com.restapi.styleswap.payload.RegisterDto;
import com.restapi.styleswap.repository.RoleRepository;
import com.restapi.styleswap.repository.UserRepository;
import com.restapi.styleswap.security.JwtTokenProvider;
import com.restapi.styleswap.service.AuthService;
import com.restapi.styleswap.utils.managers.StripeManager;
import com.restapi.styleswap.utils.UserUtils;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final StripeManager stripeManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserUtils userUtils;

    public AuthServiceImpl(AuthenticationManager authenticationManager, UserRepository userRepository,
                           RoleRepository roleRepository, StripeManager stripeManager,
                           JwtTokenProvider jwtTokenProvider, UserUtils userUtils) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.stripeManager = stripeManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userUtils = userUtils;
    }

    @Override
    public JwtAuthResponse login(LoginDto loginDto) {
        Authentication authentication = authenticateUser(loginDto);
        User user = userRepository.findByUsernameOrEmail(
                                        loginDto.getUsernameOrEmail(),
                                        loginDto.getUsernameOrEmail()).get();

        return createAuthResponse(user, authentication);
    }

    @Override
    @Transactional
    public String register(RegisterDto registerDto) throws StripeException {

        userUtils.validateUsernameAndEmailAvailability(registerDto);
        Role userRole = roleRepository.findByNameContainingIgnoreCase("ROLE_USER").get();

        Account stripeAccount = stripeManager.createStripeAccount(registerDto);
        userUtils.createAndSaveUserEntity(registerDto, stripeAccount, userRole);

        return stripeManager.generateStripeRegisterLink(stripeAccount);
    }

    private Authentication authenticateUser(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getUsernameOrEmail(), loginDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

    private JwtAuthResponse createAuthResponse(User user, Authentication authentication) {
        return JwtAuthResponse.builder()
                .accessToken(jwtTokenProvider.generateToken(authentication))
                .tokenType("Bearer")
                .role(user.getRoles().stream().map(Role::getName).toList().toString())
                .userId(user.getId())
                .usernameOrEmail(authentication.getName())
                .build();
    }
}
