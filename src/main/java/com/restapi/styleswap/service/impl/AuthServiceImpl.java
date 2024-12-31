package com.restapi.styleswap.service.impl;

import com.restapi.styleswap.entity.Role;
import com.restapi.styleswap.entity.User;
import com.restapi.styleswap.exception.ApiException;
import com.restapi.styleswap.payload.JwtAuthResponse;
import com.restapi.styleswap.payload.LoginDto;
import com.restapi.styleswap.payload.RegisterDto;
import com.restapi.styleswap.repository.RoleRepository;
import com.restapi.styleswap.repository.UserRepository;
import com.restapi.styleswap.security.JwtTokenProvider;
import com.restapi.styleswap.service.AuthService;
import com.restapi.styleswap.utils.UserUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginDto.getUsernameOrEmail(), loginDto.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userRepository.findByUsernameOrEmail(loginDto.getUsernameOrEmail(),
                                                            loginDto.getUsernameOrEmail()).get();

            return createAuthResponse(user, authentication);

        } catch (BadCredentialsException exception){
            throw new ApiException(HttpStatus.FORBIDDEN, "Wrong username/email or password");
        }
    }

    private JwtAuthResponse createAuthResponse(User user, Authentication authentication) {
        JwtAuthResponse authResponse = new JwtAuthResponse();
        authResponse.setAccessToken(jwtTokenProvider.generateToken(authentication));
        var roles = user.getRoles().stream().map(Role::getName).toList();
        authResponse.setRole(roles.toString());
        authResponse.setUserId(user.getId());
        authResponse.setUsernameOrEmail(authentication.getName());

        return authResponse;
    }

    @Override
    @Transactional
    public String register(RegisterDto registerDto) {

        userUtils.validateUsernameAndEmail(registerDto);
        Role userRole = roleRepository.findByName("ROLE_USER").get();

        User user = User.builder()
                        .email(registerDto.getEmail())
                        .password(passwordEncoder.encode(registerDto.getPassword()))
                        .name(registerDto.getName())
                        .username(registerDto.getUsername())
                        .roles(Set.of(userRole))
                        .build();

        userRepository.save(user);

        return "User sign up successfully";
    }
}
