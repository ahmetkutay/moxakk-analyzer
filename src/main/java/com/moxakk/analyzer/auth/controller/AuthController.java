package com.moxakk.analyzer.auth.controller;

import com.moxakk.analyzer.auth.dto.LoginRequest;
import com.moxakk.analyzer.auth.dto.RegisterRequest;
import com.moxakk.analyzer.auth.dto.UserDto;
import com.moxakk.analyzer.auth.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Show login page
     */
    @GetMapping("/login")
    public String showLoginPage() {
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
     * Handle login form submission
     */
    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginRequest loginRequest,
                        HttpServletResponse response,
                        Model model) {
        try {
            String token = authService.login(loginRequest);

            // Set JWT token as cookie
            Cookie cookie = new Cookie("auth_token", token);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(86400); // 1 day
            response.addCookie(cookie);

            // Redirect to dashboard
            return "redirect:/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Login failed: " + e.getMessage());
            return "auth/login";
        }
    }

    /**
     * Handle registration form submission
     */
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute RegisterRequest registerRequest,
                          Model model) {
        try {
            UserDto userDto = authService.register(registerRequest);
            model.addAttribute("success", "Registration successful! Please login.");
            return "auth/login";
        } catch (Exception e) {
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            return "auth/register";
        }
    }

    /**
     * Handle logout
     */
    @GetMapping("/logout")
    public String logout(@CookieValue(name = "auth_token", required = false) String token,
                        HttpServletResponse response) {
        if (token != null) {
            // Clear the cookie
            Cookie cookie = new Cookie("auth_token", null);
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);

            // Logout from Supabase
            authService.logout(token);
        }

        return "redirect:/auth/login";
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
}