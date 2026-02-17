package com.example.projectgrupo6.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.projectgrupo6.domain.User;

import java.util.List;


public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}