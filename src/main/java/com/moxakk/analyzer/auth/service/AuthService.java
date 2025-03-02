package com.moxakk.analyzer.auth.service;

import com.moxakk.analyzer.auth.dto.LoginRequest;
import com.moxakk.analyzer.auth.dto.RegisterRequest;
import com.moxakk.analyzer.auth.dto.UserDto;
import com.moxakk.analyzer.auth.model.User;
import com.moxakk.analyzer.auth.util.JwtUtil;
import com.moxakk.analyzer.common.exception.UnauthorizedException;
import io.github.supabase.gotrue.GoTrueClient;
import io.github.supabase.gotrue.dto.SignInWithPasswordCredentials;
import io.github.supabase.gotrue.dto.SignUpWithPasswordCredentials;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final GoTrueClient goTrueClient;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    /**
     * Register a new user with Supabase auth and create a local record
     */
    public UserDto register(RegisterRequest registerRequest) {
        try {
            // First, register the user with Supabase
            var signUpRequest = new SignUpWithPasswordCredentials(
                registerRequest.getEmail(),
                registerRequest.getPassword(),
                new HashMap<>());

            var response = goTrueClient.signUpWith(signUpRequest).blockingGet();

            if (response.getUser() == null) {
                throw new RuntimeException("Failed to register user with Supabase");
            }

            // Create the local user record
            User newUser = User.builder()
                .email(registerRequest.getEmail())
                .fullName(registerRequest.getFullName())
                .role("USER")
                .supabaseUserId(response.getUser().getId())
                .createdAt(LocalDateTime.now())
                .build();

            return userService.createUser(newUser);
        } catch (Exception e) {
            throw new RuntimeException("Registration failed: " + e.getMessage(), e);
        }
    }

    /**
     * Login a user with Supabase and return JWT token
     */
    public String login(LoginRequest loginRequest) {
        try {
            // Sign in with Supabase
            var signInRequest = new SignInWithPasswordCredentials(
                loginRequest.getEmail(),
                loginRequest.getPassword());

            var response = goTrueClient.signInWith(signInRequest).blockingGet();

            if (response.getUser() == null) {
                throw new UnauthorizedException("Invalid credentials");
            }

            // Update last login time
            userService.updateLastLogin(loginRequest.getEmail());

            // Generate JWT token
            return jwtUtil.generateToken(loginRequest.getEmail());
        } catch (Exception e) {
            throw new UnauthorizedException("Login failed: " + e.getMessage());
        }
    }

    /**
     * Sign out the user from Supabase
     */
    public void logout(String accessToken) {
        try {
            goTrueClient.signOut(accessToken).blockingAwait();
        } catch (Exception e) {
            throw new RuntimeException("Logout failed: " + e.getMessage(), e);
        }
    }
}