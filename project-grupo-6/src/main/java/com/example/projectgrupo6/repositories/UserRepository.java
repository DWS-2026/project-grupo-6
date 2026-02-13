package com.example.projectgrupo6.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.projectgrupo6.domain.User;


public interface UserRepository extends JpaRepository<User, Long> {

}