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
import com.restapi.styleswap.utils.StripeManager;
import com.restapi.styleswap.utils.UserUtils;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    private final JwtTokenProvider jwtTokenProvider;
    private final UserUtils userUtils;

    public AuthServiceImpl(AuthenticationManager authenticationManager, UserRepository userRepository,
                           RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider, UserUtils userUtils) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userUtils = userUtils;
    }

    @Override
    public JwtAuthResponse login(LoginDto loginDto){
        Authentication authentication = authenticateUser(loginDto);
        User user = userRepository.findByUsernameOrEmail(
                                        loginDto.getUsernameOrEmail(),
                                        loginDto.getUsernameOrEmail()).get();

        return createAuthResponse(user, authentication);
    }

    @Override
    @Transactional
    public String register(RegisterDto registerDto) throws StripeException {

        userUtils.validateUsernameAndEmail(registerDto);
        Role userRole = roleRepository.findByName("ROLE_USER").get();

        Account stripeAccount = StripeManager.createStripeAccount(registerDto.getEmail());
        String stripeRegisterLink = StripeManager.generateStripeRegisterLink(stripeAccount);

        saveUserEntity(registerDto, stripeAccount, userRole);

        return stripeRegisterLink;
    }

    private void saveUserEntity(RegisterDto registerDto, Account stripeAccount, Role userRole) {
        User user = User.builder()
                .email(registerDto.getEmail())
                .password(passwordEncoder.encode(registerDto.getPassword()))
                .name(registerDto.getName())
                .username(registerDto.getUsername())
                .stripeAccountId(stripeAccount.getId())
                .roles(Set.of(userRole))
                .build();

        userRepository.save(user);
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
                .role(user.getRoles().stream().map(Role::getName).toList().toString())
                .userId(user.getId())
                .usernameOrEmail(authentication.getName())
                .build();
    }
}
