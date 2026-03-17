package com.example.projectgrupo6.repositories;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.projectgrupo6.domain.User;

import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername (String username);
}