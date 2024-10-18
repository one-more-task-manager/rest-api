package com.danyatheworst.task_manager.user;

import com.danyatheworst.task_manager.exceptions.InvalidCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return this.userRepository
                .findByEmail(username)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));
    }
}