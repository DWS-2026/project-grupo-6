package com.example.projectgrupo6.security;

import com.example.projectgrupo6.domain.User;
import com.example.projectgrupo6.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class RepositoryUserDetailsService implements UserDetailsService {

    // Inject our repository
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        
        // 1. Find user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No se encontró el usuario con email: " + email));

        // 2. Construct the object
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail()) // Email as identifier
                .password(user.getEncodedPassword()) // Password
                .roles(user.getRol().toUpperCase()) // Rol (ex: "USER" o "ADMIN")
                .build();
    }
}