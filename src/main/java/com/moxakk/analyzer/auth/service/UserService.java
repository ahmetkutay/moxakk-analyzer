package com.moxakk.analyzer.auth.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import com.moxakk.analyzer.auth.dto.UserDto;
import com.moxakk.analyzer.auth.model.User;
import com.moxakk.analyzer.auth.repository.UserRepository;
import com.moxakk.analyzer.common.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    /**
     * Create a new user
     */
    public UserDto createUser(User user) {
        log.info("Creating user with email: {}", user.getEmail());
        User savedUser = userRepository.save(user);
        return mapToDto(savedUser);
    }

    /**
     * Get user by email
     */
    public User getUserByEmail(String email) {
        log.info("Getting user by email: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", email);
                    return new ResourceNotFoundException("User not found with email: " + email);
                });
    }

    /**
     * Get user by ID
     */
    public UserDto getUserById(UUID id) {
        log.info("Getting user by ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", id);
                    return new ResourceNotFoundException("User not found with id: " + id);
                });

        return mapToDto(user);
    }

    /**
     * Check if user exists by email
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Update user's last login time
     */
    public void updateLastLogin(String email) {
        log.info("Updating last login for user: {}", email);
        User user = getUserByEmail(email);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
    }

    /**
     * Create Spring Security Authentication object from user email
     */
    public Authentication createAuthentication(String email) {
        User user = getUserByEmail(email);

        var authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole()));

        return new UsernamePasswordAuthenticationToken(
                email, null, authorities);
    }

    /**
     * Map User entity to UserDto
     */
    private UserDto mapToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .lastLogin(user.getLastLogin())
                .build();
    }
}