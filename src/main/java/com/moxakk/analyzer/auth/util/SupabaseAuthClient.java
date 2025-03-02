package com.moxakk.analyzer.auth.util;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * Client for interacting with Supabase Auth API
 */
@Component
@RequiredArgsConstructor
public class SupabaseAuthClient {

    private final WebClient supabaseWebClient;
    private static final Logger log = LoggerFactory.getLogger(SupabaseAuthClient.class);

    /**
     * Sign up a new user with Supabase
     */
    public Mono<SupabaseAuthResponse> signUp(String email, String password) {
        Map<String, Object> signUpRequest = Map.of(
            "email", email,
            "password", password
        );

        log.info("Attempting to sign up user with Supabase: {}", email);

        return supabaseWebClient.post()
            .uri("/auth/v1/signup")
            .bodyValue(signUpRequest)
            .retrieve()
            .bodyToMono(SupabaseAuthResponse.class)
            .doOnSuccess(response -> {
                if (response != null && response.getUser() != null) {
                    log.info("Successfully signed up user with Supabase: {}", email);
                } else {
                    log.warn("Supabase sign-up response was empty or missing user data for: {}", email);
                }
            })
            .doOnError(error -> {
                log.error("Failed to sign up user with Supabase: {} - Error: {}", email, error.getMessage(), error);
            });
    }

    /**
     * Sign in a user with Supabase
     */
    public Mono<SupabaseAuthResponse> signIn(String email, String password) {
        Map<String, Object> signInRequest = Map.of(
            "email", email,
            "password", password
        );

        log.info("Attempting to sign in user with Supabase: {}", email);

        return supabaseWebClient.post()
            .uri("/auth/v1/token?grant_type=password")
            .bodyValue(signInRequest)
            .retrieve()
            .bodyToMono(SupabaseAuthResponse.class)
            .doOnSuccess(response -> {
                if (response != null && response.getUser() != null) {
                    log.info("Successfully signed in user with Supabase: {}", email);
                } else {
                    log.warn("Supabase sign-in response was empty or missing user data for: {}", email);
                }
            })
            .doOnError(error -> {
                log.error("Failed to sign in user with Supabase: {} - Error: {}", email, error.getMessage(), error);
            });
    }

    /**
     * Sign out a user from Supabase
     */
    public Mono<Void> signOut(String accessToken) {
        return supabaseWebClient.post()
            .uri("/auth/v1/logout")
            .header("Authorization", "Bearer " + accessToken)
            .retrieve()
            .bodyToMono(Void.class);
    }

    /**
     * Reset password for a user
     */
    public Mono<Void> resetPassword(String email) {
        Map<String, Object> resetRequest = Map.of(
            "email", email
        );

        return supabaseWebClient.post()
            .uri("/auth/v1/recover")
            .bodyValue(resetRequest)
            .retrieve()
            .bodyToMono(Void.class);
    }

    /**
     * Response class for Supabase Auth API
     */
    public static class SupabaseAuthResponse {
        private SupabaseUser user;
        private String accessToken;
        private String refreshToken;

        public SupabaseUser getUser() {
            return user;
        }

        public void setUser(SupabaseUser user) {
            this.user = user;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }
    }

    /**
     * User class for Supabase Auth API
     */
    public static class SupabaseUser {
        private String id;
        private String email;
        private String phone;
        private Map<String, Object> userMetadata;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public Map<String, Object> getUserMetadata() {
            return userMetadata;
        }

        public void setUserMetadata(Map<String, Object> userMetadata) {
            this.userMetadata = userMetadata;
        }
    }
}
