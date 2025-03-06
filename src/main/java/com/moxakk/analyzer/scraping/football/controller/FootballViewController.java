package com.moxakk.analyzer.scraping.football.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for football match analysis views.
 * This controller provides endpoints for rendering football match analysis pages.
 */
@Controller
@RequestMapping("/football")
public class FootballViewController {

    /**
     * Renders the football match analysis page.
     *
     * @return The name of the view to render
     */
    @GetMapping("/analysis")
    public String showAnalysisPage() {
        return "dashboard/football-analysis";
    }
}