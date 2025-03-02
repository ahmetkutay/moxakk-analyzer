package com.moxakk.analyzer.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.web.filter.OncePerRequestFilter;

import com.moxakk.analyzer.auth.service.CustomUserDetailsService;
import com.moxakk.analyzer.auth.service.UserService;
import com.moxakk.analyzer.auth.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        // Set the name of the attribute the CsrfToken will be populated on
        requestHandler.setCsrfRequestAttributeName("_csrf");

        http
            // Configure CSRF protection
            .csrf(csrf -> csrf
                // Use Cookie based CSRF protection
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(requestHandler)
                // Disable CSRF for API endpoints
                .ignoringRequestMatchers("/auth/api/**"))

            // Configure authorization rules
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/", "/auth/**", "/css/**", "/js/**", "/img/**", "/favicon.ico").permitAll()
                .anyRequest().authenticated())

            // Disable form login since we're using our custom login endpoint
            .formLogin(form -> form.disable())

            // Configure logout
            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/auth/login?logout=true")
                .deleteCookies("auth_token", "JSESSIONID", "remember-me")
                .permitAll())

            // Configure remember-me
            .rememberMe(remember -> remember
                .key("analyzer-remember-me-key")
                .tokenValiditySeconds(86400 * 30) // 30 days
                .rememberMeParameter("remember")
                .userDetailsService(userDetailsService()))

            // Configure session management
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))

            // Add JWT authentication filter
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)

            // Set authentication provider
            .authenticationProvider(authenticationProvider())

            // Configure security headers
            .headers(headers -> headers
                .xssProtection(xss -> xss
                    .headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
                .contentSecurityPolicy(csp -> csp
                    .policyDirectives("default-src 'self'; script-src 'self' 'unsafe-inline' 'unsafe-eval' https://unpkg.com https://cdn.tailwindcss.com https://cdn.jsdelivr.net; style-src 'self' 'unsafe-inline' https://unpkg.com; img-src 'self' data:; connect-src 'self'"))
                .frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }

    @Bean
    public OncePerRequestFilter jwtAuthenticationFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(@NonNull jakarta.servlet.http.HttpServletRequest request,
                                           @NonNull jakarta.servlet.http.HttpServletResponse response,
                                           @NonNull jakarta.servlet.FilterChain filterChain)
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
                // First try to get from Authorization header
                String bearerToken = request.getHeader("Authorization");
                if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
                    return bearerToken.substring(7);
                }

                // Then try to get from cookie
                jakarta.servlet.http.Cookie[] cookies = request.getCookies();
                if (cookies != null) {
                    for (jakarta.servlet.http.Cookie cookie : cookies) {
                        if ("auth_token".equals(cookie.getName())) {
                            return cookie.getValue();
                        }
                    }
                }

                return null;
            }
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService(userService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}