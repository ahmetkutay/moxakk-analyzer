package com.moxakk.analyzer.auth.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.moxakk.analyzer.auth.dto.LoginRequest;
import com.moxakk.analyzer.auth.dto.RegisterRequest;
import com.moxakk.analyzer.auth.dto.UserDto;
import com.moxakk.analyzer.auth.service.AuthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    /**
     * Show login page
     */
    @GetMapping("/login")
    public String showLoginPage(@RequestParam(required = false) String error,
                               @RequestParam(required = false) String logout,
                               Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password");
        }

        if (logout != null) {
            model.addAttribute("success", "You have been logged out successfully");
        }

        return "auth/login";
    }

    /**
     * Show registration page
     */
    @GetMapping("/register")
    public String showRegisterPage() {
        return "auth/register";
    }

    /**
     * Show forgot password page
     */
    @GetMapping("/forgot-password")
    public String showForgotPasswordPage() {
        return "auth/forgot-password";
    }

    /**
     * Handle password reset request
     */
    @PostMapping("/forgot-password")
    public String requestPasswordReset(@RequestParam String email, Model model) {
        try {
            authService.requestPasswordReset(email);
            model.addAttribute("success", "Password reset instructions have been sent to your email.");
            return "auth/login";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to request password reset: " + e.getMessage());
            return "auth/forgot-password";
        }
    }

    /**
     * Handle login form submission
     */
    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginRequest loginRequest,
                        HttpServletResponse response,
                        HttpServletRequest request,
                        Model model) {
        try {
            log.info("Processing login request for user: {}", loginRequest.getEmail());

            String token = authService.login(loginRequest);
            log.info("Login successful for user: {}", loginRequest.getEmail());

            // Set JWT token as cookie
            Cookie cookie = new Cookie("auth_token", token);
            cookie.setPath("/");
            cookie.setHttpOnly(false); // Allow JavaScript access
            cookie.setMaxAge(86400); // 1 day
            cookie.setSecure(false); // Allow non-HTTPS for development
            response.addCookie(cookie);

            log.info("Auth token cookie set for user: {}", loginRequest.getEmail());

            // Check if this is an HTMX request
            String hxRequest = request.getHeader("HX-Request");
            log.info("HX-Request header: {}", hxRequest);

            if (hxRequest != null) {
                log.info("HTMX request detected for login");
                // For HTMX requests, return a fragment with success message
                model.addAttribute("success", "Login successful!");
                return "fragments/alert :: success";
            }

            // For regular form submissions, redirect to dashboard
            log.info("Redirecting to dashboard after successful login");
            return "redirect:/dashboard";
        } catch (Exception e) {
            log.error("Login failed for user {}: {}", loginRequest.getEmail(), e.getMessage(), e);
            model.addAttribute("error", "Login failed: " + e.getMessage());

            // Check if this is an HTMX request
            String hxRequest = request.getHeader("HX-Request");
            if (hxRequest != null) {
                log.info("HTMX request detected for login error response");
                // For HTMX requests, return a fragment with error message
                return "fragments/alert :: error";
            }

            return "auth/login";
        }
    }

    /**
     * Handle registration form submission
     */
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute RegisterRequest registerRequest,
                          Model model) {
        log.info("Processing registration request for: {}", registerRequest.getEmail());
        try {
            UserDto userDto = authService.register(registerRequest);
            log.info("Registration successful for: {}", userDto.getEmail());
            model.addAttribute("success", "Registration successful for " + userDto.getEmail() + "! Please login.");
            return "auth/login";
        } catch (Exception e) {
            log.error("Registration failed for {}: {}", registerRequest.getEmail(), e.getMessage(), e);
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            // Return to the register page with the form data preserved
            model.addAttribute("registerRequest", registerRequest);
            return "auth/register";
        }
    }

    /**
     * Handle logout
     */
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        log.info("Processing logout request");

        // Get the token from the cookie
        String token = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("auth_token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        // Clear the cookie regardless of token presence
        Cookie cookie = new Cookie("auth_token", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        log.info("Auth token cookie cleared");

        try {
            // Logout from Supabase if we have a token
            if (token != null && !token.isEmpty()) {
                log.info("Attempting to logout from Supabase");
                authService.logout(token);
            } else {
                log.info("No auth token found in cookies, skipping Supabase logout");
            }
        } catch (Exception e) {
            // Log the error but continue with logout
            log.error("Error during logout process: {}", e.getMessage(), e);
        }

        log.info("Logout completed, redirecting to login page");
        return "redirect:/auth/login?logout=true";
    }

    /**
     * REST API for login (for HTMX or SPA clients)
     */
    @PostMapping("/api/login")
    @ResponseBody
    public ResponseEntity<?> apiLogin(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            String token = authService.login(loginRequest);
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * REST API for registration (for HTMX or SPA clients)
     */
    @PostMapping("/api/register")
    @ResponseBody
    public ResponseEntity<?> apiRegister(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            UserDto userDto = authService.register(registerRequest);
            return ResponseEntity.ok(userDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * REST API for password reset (for HTMX or SPA clients)
     */
    @PostMapping("/api/forgot-password")
    @ResponseBody
    public ResponseEntity<?> apiForgotPassword(@RequestParam String email) {
        try {
            authService.requestPasswordReset(email);
            return ResponseEntity.ok(Map.of("message", "Password reset instructions have been sent to your email."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}