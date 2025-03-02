package com.moxakk.analyzer.auth.service;

import com.moxakk.analyzer.auth.dto.UserDto;
import com.moxakk.analyzer.auth.model.User;
import com.moxakk.analyzer.common.exception.ResourceNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    // In a real application, this would connect to a repository
    // For demonstration purposes, we're using an in-memory list
    private final List<User> users = Collections.synchronizedList(new java.util.ArrayList<>());

    /**
     * Create a new user
     */
    public UserDto createUser(User user) {
        // In a real application, this would save to a database
        users.add(user);
        return mapToDto(user);
    }

    /**
     * Get user by email
     */
    public User getUserByEmail(String email) {
        return users.stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    /**
     * Get user by ID
     */
    public UserDto getUserById(UUID id) {
        User user = users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        return mapToDto(user);
    }

    /**
     * Update user's last login time
     */
    public void updateLastLogin(String email) {
        User user = getUserByEmail(email);
        user.setLastLogin(LocalDateTime.now());
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