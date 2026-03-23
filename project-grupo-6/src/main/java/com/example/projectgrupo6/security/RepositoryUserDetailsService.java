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

    // Inyectamos TU repositorio de siempre
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        
        // 1. Usamos tu código para buscar al usuario por email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No se encontró el usuario con email: " + email));

        // 2. Construimos el objeto que Spring Security entiende
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail()) // Le pasamos el email como identificador
                .password(user.getEncodedPassword()) // Le pasamos tu variable exacta
                .roles(user.getRol().toUpperCase()) // Le pasamos el rol (ej: "USER" o "ADMIN")
                .build();
    }
}