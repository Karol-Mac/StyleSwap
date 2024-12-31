package com.restapi.styleswap.security;

import com.restapi.styleswap.entity.User;
import com.restapi.styleswap.utils.UserUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserUtils userUtils;

    public CustomUserDetailsService(UserUtils userUtils) {
        this.userUtils = userUtils;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User user = userUtils.getUser(usernameOrEmail, usernameOrEmail);

        //Set<Role> -> Set<GrantedAuthority>. It's mandatory to create a User object <- from spring security User
        Set<GrantedAuthority> authoritySet = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), authoritySet);
    }
}
