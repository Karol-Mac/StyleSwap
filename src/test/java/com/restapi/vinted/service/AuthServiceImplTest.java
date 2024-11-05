package com.restapi.vinted.service;

import com.restapi.vinted.entity.Role;
import com.restapi.vinted.entity.User;
import com.restapi.vinted.exception.ApiException;
import com.restapi.vinted.payload.JwtAuthResponse;
import com.restapi.vinted.payload.LoginDto;
import com.restapi.vinted.payload.RegisterDto;
import com.restapi.vinted.repository.RoleRepository;
import com.restapi.vinted.repository.UserRepository;
import com.restapi.vinted.security.JwtTokenProvider;
import com.restapi.vinted.service.impl.AuthServiceImpl;
import com.restapi.vinted.utils.UserUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserUtils userUtils;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loginWithValidCredentialsReturnsJwtAuthResponse() {
        LoginDto loginDto = new LoginDto("user", "password");
        Authentication authentication = mock(Authentication.class);
        User user = mock(User.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(userRepository.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.of(user));
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("token");
        when(user.getRoles()).thenReturn(Set.of(new Role(1L, "ROLE_USER")));
        when(user.getId()).thenReturn(1L);
        when(user.getUsername()).thenReturn("user");

        JwtAuthResponse response = authService.login(loginDto);

        assertNotNull(response);
        assertEquals("token", response.getAccessToken());
        assertEquals("[ROLE_USER]", response.getRole());
        assertEquals(1L, response.getUserId());
    }

    @Test
    void loginWithInvalidCredentialsThrowsApiException() {
        LoginDto loginDto = new LoginDto("user", "wrongpassword");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new BadCredentialsException("Bad credentials"));

        ApiException exception = assertThrows(ApiException.class, () -> authService.login(loginDto));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        assertEquals("Wrong username/email or password", exception.getMessage());
    }

    @Test
    void registerWithValidDataReturnsSuccessMessage() {
        RegisterDto registerDto = new RegisterDto("user", "password", "email@example.com", "name");
        Role role = new Role(0L, "ROLE_USER");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        String result = authService.register(registerDto);

        assertEquals("User sign up successfully", result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerWithExistingUsernameOrEmailThrowsApiException() {
        RegisterDto registerDto = new RegisterDto("user", "password", "email@example.com", "name");
        doThrow(new ApiException(HttpStatus.BAD_REQUEST, "Username or Email already exists")).when(userUtils).validateUsernameAndEmail(registerDto);

        ApiException exception = assertThrows(ApiException.class, () -> authService.register(registerDto));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Username or Email already exists", exception.getMessage());
    }
}