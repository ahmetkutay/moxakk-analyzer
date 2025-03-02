package com.moxakk.analyzer.auth.service;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.moxakk.analyzer.common.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Loading user by username: {}", username);
        try {
            // Find the user by email (username)
            com.moxakk.analyzer.auth.model.User user = userService.getUserByEmail(username);
            log.info("Found user: {}", user.getEmail());

            // Create authorities
            var authorities = Collections.singletonList(
                    new SimpleGrantedAuthority("ROLE_" + user.getRole()));

            // Return Spring Security UserDetails
            return new User(
                    user.getEmail(),
                    // We don't store passwords locally, so use a placeholder
                    // The actual authentication is done by Supabase
                    "{noop}placeholder",
                    true, true, true, true,
                    authorities);
        } catch (ResourceNotFoundException e) {
            log.error("User not found with email: {}", username);
            throw new UsernameNotFoundException("User not found with email: " + username, e);
        } catch (Exception e) {
            log.error("Failed to load user by username: {}", username, e);
            throw new UsernameNotFoundException("Error loading user with email: " + username, e);
        }
    }
}