package com.restapi.styleswap.service;

import com.restapi.styleswap.entity.Role;
import com.restapi.styleswap.entity.User;
import com.restapi.styleswap.payload.JwtAuthResponse;
import com.restapi.styleswap.payload.LoginDto;
import com.restapi.styleswap.payload.RegisterDto;
import com.restapi.styleswap.repository.RoleRepository;
import com.restapi.styleswap.repository.UserRepository;
import com.restapi.styleswap.security.JwtTokenProvider;
import com.restapi.styleswap.service.impl.AuthServiceImpl;
import com.restapi.styleswap.utils.StripeManager;
import com.restapi.styleswap.utils.UserUtils;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private StripeManager stripeManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserUtils userUtils;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login_returnsJwtAuthResponse() {
        LoginDto loginDto = new LoginDto();
        loginDto.setUsernameOrEmail("user@example.com");
        loginDto.setPassword("password");

        Authentication authentication = mock(Authentication.class);
        User user = new User();
        user.setId(1L);
        user.setUsername("user");
        user.setEmail("user@example.com");
        user.setRoles(Set.of(new Role(0L, "ROLE_USER")));

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(userRepository.findByUsernameOrEmail("user@example.com", "user@example.com")).thenReturn(Optional.of(user));
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("jwt-token");

        JwtAuthResponse response = authService.login(loginDto);
        assertNotNull(response);
        assertEquals("jwt-token", response.getAccessToken());
        assertEquals("[ROLE_USER]", response.getRole());
        assertEquals(1L, response.getUserId());
    }

    @Test
    void login_throwsExceptionWhenUserNotFound() {
        LoginDto loginDto = new LoginDto();
        loginDto.setUsernameOrEmail("user@example.com");
        loginDto.setPassword("password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mock(Authentication.class));
        when(userRepository.findByUsernameOrEmail("user@example.com", "user@example.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.login(loginDto));
    }

    @Test
    void register_createsAndReturnsStripeLink() throws StripeException {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail("user@example.com");
        registerDto.setPassword("password");
        registerDto.setFirstName("First");
        registerDto.setLastName("Last");
        registerDto.setPhoneNumber("375649123");
        registerDto.setUsername("user");

        Role userRole = new Role(0L,"ROLE_USER");
        Account stripeAccount = mock(Account.class);
        when(stripeAccount.getId()).thenReturn("acct_123");

        doNothing().when(userUtils).checkIfUsernameOfEmailExist(registerDto);
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));

        when(stripeManager.createStripeAccount(registerDto)).thenReturn(stripeAccount);
        when(stripeManager.generateStripeRegisterLink(stripeAccount)).thenReturn("stripe-link");

        String result = authService.register(registerDto);

        assertEquals("stripe-link", result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_throwsExceptionWhenRoleNotFound() {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail("user@example.com");
        registerDto.setPassword("password");
        registerDto.setFirstName("First");
        registerDto.setLastName("Last");
        registerDto.setPhoneNumber("1234567890");
        registerDto.setUsername("user");

        doNothing().when(userUtils).checkIfUsernameOfEmailExist(registerDto);
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.register(registerDto));
    }
}