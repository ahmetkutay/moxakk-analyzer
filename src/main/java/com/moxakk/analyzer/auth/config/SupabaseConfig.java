package com.moxakk.analyzer.auth.config;

import io.github.supabase.CreateClientOptions;
import io.github.supabase.SupabaseClient;
import io.github.supabase.gotrue.GoTrueClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SupabaseConfig {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @Bean
    public SupabaseClient supabaseClient() {
        return new SupabaseClient(supabaseUrl, supabaseKey,
                                 new CreateClientOptions());
    }

    @Bean
    public GoTrueClient goTrueClient(SupabaseClient supabaseClient) {
        return supabaseClient.getAuth();
    }
}