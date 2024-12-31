package com.restapi.styleswap.utils;

import com.restapi.styleswap.entity.User;
import com.restapi.styleswap.exception.ApiException;
import com.restapi.styleswap.exception.ResourceNotFoundException;
import com.restapi.styleswap.payload.RegisterDto;
import com.restapi.styleswap.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserUtils {
    private final UserRepository userRepository;

    public UserUtils(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public void validateUsernameAndEmail(RegisterDto registerDto){
        if(userRepository.existsByUsername(registerDto.getUsername()))
            throw new ApiException(HttpStatus.BAD_REQUEST, "Username is already taken");
        if(userRepository.existsByEmail(registerDto.getEmail()))
            throw new ApiException(HttpStatus.BAD_REQUEST, "Email already exist");
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
}
