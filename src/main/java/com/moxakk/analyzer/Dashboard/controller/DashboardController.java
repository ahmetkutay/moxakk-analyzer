package com.moxakk.analyzer.Dashboard.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/dashboard")
@Slf4j
public class DashboardController {

    @GetMapping
    public String dashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Dashboard accessed, authentication: {}", authentication);

        if (authentication != null && authentication.isAuthenticated() &&
            !authentication.getPrincipal().equals("anonymousUser")) {
            model.addAttribute("email", authentication.getName());
            model.addAttribute("authorities", authentication.getAuthorities());
            model.addAttribute("isAuthenticated", true);
            log.info("User is authenticated: {}", authentication.getName());
        } else {
            model.addAttribute("email", "Guest");
            model.addAttribute("isAuthenticated", false);
            log.info("User is not authenticated");
        }

        return "dashboard/index";
    }
}