package com.moxakk.analyzer.auth.config;

import com.moxakk.analyzer.auth.service.UserService;
import com.moxakk.analyzer.auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)  // Disable CSRF for API requests
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/", "/auth/**", "/css/**", "/js/**", "/img/**").permitAll()
                .anyRequest().authenticated())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public OncePerRequestFilter jwtAuthenticationFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(jakarta.servlet.http.HttpServletRequest request,
                                           jakarta.servlet.http.HttpServletResponse response,
                                           jakarta.servlet.FilterChain filterChain)
                                           throws jakarta.servlet.ServletException, java.io.IOException {

                try {
                    String token = extractTokenFromRequest(request);
                    if (token != null && jwtUtil.validateToken(token)) {
                        String email = jwtUtil.getEmailFromToken(token);
                        // Set authentication in security context
                        var authentication = userService.createAuthentication(email);
                        org.springframework.security.core.context.SecurityContextHolder.getContext()
                            .setAuthentication(authentication);
                    }
                } catch (Exception e) {
                    // Log exception but don't prevent the request from proceeding
                    System.err.println("Could not set authentication: " + e.getMessage());
                }

                filterChain.doFilter(request, response);
            }

            private String extractTokenFromRequest(jakarta.servlet.http.HttpServletRequest request) {
                String bearerToken = request.getHeader("Authorization");
                if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
                    return bearerToken.substring(7);
                }
                return null;
            }
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}