package com.moxakk.analyzer.auth.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.moxakk.analyzer.auth.dto.LoginRequest;
import com.moxakk.analyzer.auth.dto.RegisterRequest;
import com.moxakk.analyzer.auth.dto.UserDto;
import com.moxakk.analyzer.auth.model.User;
import com.moxakk.analyzer.auth.util.JwtUtil;
import com.moxakk.analyzer.auth.util.SupabaseAuthClient;
import com.moxakk.analyzer.common.exception.ResourceNotFoundException;
import com.moxakk.analyzer.common.exception.UnauthorizedException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final SupabaseAuthClient supabaseAuthClient;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    /**
     * Register a new user with Supabase and create a local user record
     */
    public UserDto register(RegisterRequest registerRequest) {
        log.info("Processing registration request for: {}", registerRequest.getEmail());

        // Check if user already exists in our database
        if (userService.existsByEmail(registerRequest.getEmail())) {
            log.error("User already exists in local database: {}", registerRequest.getEmail());
            throw new IllegalArgumentException("User with this email already exists");
        }

        try {
            // Try to sign up with Supabase
            log.info("Attempting to register user with Supabase: {}", registerRequest.getEmail());
            SupabaseAuthClient.SupabaseAuthResponse response = supabaseAuthClient
                .signUp(registerRequest.getEmail(), registerRequest.getPassword())
                .block();

            if (response == null || response.getUser() == null) {
                log.error("Failed to register user with Supabase: {}", registerRequest.getEmail());
                throw new IllegalArgumentException("Registration failed");
            }

            log.info("Successfully registered user with Supabase: {}", registerRequest.getEmail());
            log.info("Supabase user ID: {}", response.getUser().getId());

            // Create user in our database
            User user = User.builder()
                .email(registerRequest.getEmail())
                .fullName(registerRequest.getFullName())
                .role("USER")
                .supabaseUserId(response.getUser().getId())
                .createdAt(LocalDateTime.now())
                .build();

            UserDto savedUser = userService.createUser(user);
            log.info("Created local user record for: {}", registerRequest.getEmail());

            return savedUser;
        } catch (WebClientResponseException e) {
            // Check if the error is because the user already exists in Supabase
            if (e.getStatusCode().value() == 400 && e.getResponseBodyAsString().contains("already exists")) {
                log.warn("User already exists in Supabase but not in our database: {}", registerRequest.getEmail());

                try {
                    // Try to sign in with Supabase to get the user ID
                    SupabaseAuthClient.SupabaseAuthResponse response = supabaseAuthClient
                        .signIn(registerRequest.getEmail(), registerRequest.getPassword())
                        .block();

                    if (response != null && response.getUser() != null) {
                        log.info("Successfully signed in existing Supabase user: {}", registerRequest.getEmail());
                        log.info("Supabase user ID: {}", response.getUser().getId());

                        // Create user in our database
                        User user = User.builder()
                            .email(registerRequest.getEmail())
                            .fullName(registerRequest.getFullName())
                            .role("USER")
                            .supabaseUserId(response.getUser().getId())
                            .createdAt(LocalDateTime.now())
                            .build();

                        UserDto savedUser = userService.createUser(user);
                        log.info("Created local user record for existing Supabase user: {}", registerRequest.getEmail());

                        return savedUser;
                    } else {
                        log.error("Failed to sign in existing Supabase user: {}", registerRequest.getEmail());
                        throw new IllegalArgumentException("Registration failed: Unable to retrieve user details");
                    }
                } catch (Exception signInError) {
                    log.error("Error signing in existing Supabase user: {}", signInError.getMessage(), signInError);
                    throw new IllegalArgumentException("Registration failed: " + signInError.getMessage());
                }
            }

            log.error("Registration failed due to API error: {} - Response: {}", e.getMessage(), e.getResponseBodyAsString(), e);
            throw new IllegalArgumentException("Registration failed: " + e.getMessage());
        } catch (Exception e) {
            log.error("Registration failed: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Registration failed: " + e.getMessage());
        }
    }

    /**
     * Login a user with Supabase and return JWT token
     */
    public String login(LoginRequest loginRequest) {
        try {
            log.info("Attempting login for user: {}", loginRequest.getEmail());

            // Sign in with Supabase
            SupabaseAuthClient.SupabaseAuthResponse response = supabaseAuthClient
                .signIn(loginRequest.getEmail(), loginRequest.getPassword())
                .block();

            if (response == null || response.getUser() == null) {
                log.error("Invalid credentials for user: {}", loginRequest.getEmail());
                throw new UnauthorizedException("Invalid credentials");
            }

            log.info("Supabase authentication successful for user: {}", loginRequest.getEmail());
            log.info("Supabase user ID: {}", response.getUser().getId());

            // Check if user exists in our database
            try {
                // Update last login time if user exists
                userService.updateLastLogin(loginRequest.getEmail());
                log.info("User found in local database, updated last login time: {}", loginRequest.getEmail());
            } catch (ResourceNotFoundException e) {
                // User exists in Supabase but not in our database
                // Create the user in our database
                log.info("User exists in Supabase but not in our database. Creating local record for: {}", loginRequest.getEmail());
                User newUser = User.builder()
                    .email(loginRequest.getEmail())
                    .fullName(response.getUser().getEmail()) // Use email as fullName initially
                    .role("USER")
                    .supabaseUserId(response.getUser().getId())
                    .createdAt(LocalDateTime.now())
                    .build();

                userService.createUser(newUser);
                log.info("Created local user record for: {}", loginRequest.getEmail());
            }

            // Generate JWT token
            String token = jwtUtil.generateToken(loginRequest.getEmail());
            log.info("Generated JWT token for user: {}", loginRequest.getEmail());
            return token;
        } catch (WebClientResponseException e) {
            log.error("Login failed due to API error: {} - Response: {}", e.getMessage(), e.getResponseBodyAsString(), e);
            throw new UnauthorizedException("Login failed: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("Login failed due to invalid input: {}", e.getMessage(), e);
            throw new UnauthorizedException("Login failed: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error("Login failed: {}", e.getMessage(), e);
            throw new UnauthorizedException("Login failed: " + e.getMessage());
        }
    }

    /**
     * Sign out the user from Supabase
     */
    public void logout(String accessToken) {
        try {
            if (accessToken == null || accessToken.isEmpty()) {
                log.warn("Attempted to logout with null or empty token");
                return; // Just return without error if no token
            }

            log.info("Attempting to sign out user from Supabase");
            supabaseAuthClient.signOut(accessToken).block();
            log.info("User successfully signed out from Supabase");
        } catch (org.springframework.web.reactive.function.client.WebClientResponseException.Forbidden e) {
            // Token is likely invalid or expired, which is fine for logout
            log.warn("Token was rejected during logout (403 Forbidden). User may already be logged out: {}", e.getMessage());
            // Don't rethrow - we still want to clear local session
        } catch (Exception e) {
            log.error("Logout failed with unexpected error", e);
            // Don't throw exception for logout failures - just log it
            // This ensures the user can still log out locally even if Supabase call fails
        }
    }

    /**
     * Request password reset for a user
     */
    public void requestPasswordReset(String email) {
        try {
            supabaseAuthClient.resetPassword(email).block();
        } catch (Exception e) {
            log.error("Password reset request failed", e);
            throw new RuntimeException("Password reset request failed: " + e.getMessage(), e);
        }
    }

    /**
     * Map User entity to UserDto
     */
    private UserDto mapToUserDto(User user) {
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