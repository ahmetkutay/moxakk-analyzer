package com.moxakk.analyzer.auth.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    private String fullName;

    private String role;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime lastLogin;

    // Supabase userId - important for linking with Supabase auth
    @Column(nullable = false, unique = true)
    private String supabaseUserId;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}