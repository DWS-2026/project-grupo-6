package com.example.projectgrupo6.repositories;

import com.example.projectgrupo6.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Long> {

}