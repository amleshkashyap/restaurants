package com.example.restaurant.service;

import com.example.restaurant.model.CurrentPrincipal;
import com.example.restaurant.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public CurrentPrincipal loadUserByUsername(final String username) {
        return this.userRepository
                .findOneWithRolesByEmailIgnoreCase(username)
                .map(this::createSpringSecurityUser)
                .orElseThrow(() -> new UsernameNotFoundException("Can't find username"));
    }

    private CurrentPrincipal createSpringSecurityUser(com.example.restaurant.model.User user) {
        return new CurrentPrincipal(
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getPassword(),
            user.getRole().getName()
        );
    }
}
