package com.moxakk.analyzer.auth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.moxakk.analyzer.auth.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    Optional<User> findBySupabaseUserId(String supabaseUserId);

    boolean existsByEmail(String email);
}