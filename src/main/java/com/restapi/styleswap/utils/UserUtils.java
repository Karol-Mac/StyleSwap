package com.restapi.styleswap.utils;

import com.restapi.styleswap.entity.Role;
import com.restapi.styleswap.entity.User;
import com.restapi.styleswap.exception.ApiException;
import com.restapi.styleswap.exception.ResourceNotFoundException;
import com.restapi.styleswap.payload.RegisterDto;
import com.restapi.styleswap.repository.UserRepository;
import com.stripe.model.Account;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class UserUtils {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public UserUtils(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void validateUsernameAndEmailAvailability(RegisterDto registerDto){
        if(userRepository.existsByUsername(registerDto.getUsername()))
            throw new ApiException(HttpStatus.CONFLICT, "Username is already taken");
        if(userRepository.existsByEmail(registerDto.getEmail()))
            throw new ApiException(HttpStatus.CONFLICT, "Email already exist");
    }


    public User getUser(String email){
        return userRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("User", "email", email));
    }

    public User getUser(String username, String email){
        return userRepository.findByUsernameOrEmail(username, email)
                .orElseThrow( () -> new UsernameNotFoundException(
                        "User not found with username or email: "+ (username+email)));
    }

    public void createAndSaveUserEntity(RegisterDto registerDto, Account stripeAccount, Role userRole) {

        User user = User.builder()
                .email(registerDto.getEmail())
                .password(passwordEncoder.encode(registerDto.getPassword()))
                .firstName(registerDto.getFirstName())
                .lastName(registerDto.getLastName())
                .phoneNumber(registerDto.getPhoneNumber())
                .username(registerDto.getUsername())
                .stripeAccountId(stripeAccount.getId())
                .roles(Set.of(userRole))
                .build();

        userRepository.save(user);
    }
}
