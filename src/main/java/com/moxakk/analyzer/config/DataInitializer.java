package com.moxakk.analyzer.config;

import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.moxakk.analyzer.auth.model.User;
import com.moxakk.analyzer.auth.repository.UserRepository;
import com.moxakk.analyzer.auth.util.SupabaseAuthClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final UserRepository userRepository;
    private final SupabaseAuthClient supabaseAuthClient;

    @Bean
    @Profile("!test") // Don't run in test profile
    public CommandLineRunner initData() {
        return args -> {
            log.info("Initializing database with sample data...");

            // Check if we have any users
            if (userRepository.count() == 0) {
                log.info("No users found in database. Creating test user...");

                // Create a test user in Supabase if it doesn't exist
                try {
                    // Try to sign in with test user credentials
                    SupabaseAuthClient.SupabaseAuthResponse response = supabaseAuthClient
                        .signIn("test@example.com", "password123")
                        .block();

                    if (response != null && response.getUser() != null) {
                        log.info("Test user exists in Supabase. Creating local record.");

                        // Create local user record
                        User testUser = User.builder()
                            .email("test@example.com")
                            .fullName("Test User")
                            .role("ADMIN")
                            .supabaseUserId(response.getUser().getId())
                            .createdAt(LocalDateTime.now())
                            .build();

                        userRepository.save(testUser);
                        log.info("Test user created successfully.");
                    }
                } catch (Exception e) {
                    log.warn("Could not sign in test user. Attempting to create one in Supabase...");

                    try {
                        // Create user in Supabase
                        SupabaseAuthClient.SupabaseAuthResponse response = supabaseAuthClient
                            .signUp("test@example.com", "password123")
                            .block();

                        if (response != null && response.getUser() != null) {
                            log.info("Test user created in Supabase. Creating local record.");

                            // Create local user record
                            User testUser = User.builder()
                                .email("test@example.com")
                                .fullName("Test User")
                                .role("ADMIN")
                                .supabaseUserId(response.getUser().getId())
                                .createdAt(LocalDateTime.now())
                                .build();

                            userRepository.save(testUser);
                            log.info("Test user created successfully.");
                        } else {
                            log.error("Failed to create test user in Supabase.");
                        }
                    } catch (Exception ex) {
                        log.error("Failed to create test user: {}", ex.getMessage(), ex);
                    }
                }
            } else {
                log.info("Database already contains users. Skipping initialization.");
            }
        };
    }
}