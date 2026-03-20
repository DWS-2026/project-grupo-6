package com.example.projectgrupo6.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RepositoryUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        
        // 1. Buscas TU usuario en la base de datos por email
        User miUsuario = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // 2. Traduces TU usuario al idioma de Spring Security
        return org.springframework.security.core.userdetails.User.builder()
                .username(miUsuario.getEmail()) 
                .password(miUsuario.getEncodedPassword()) // <-- ¡AQUÍ ESTÁ LA MAGIA!
                .roles(miUsuario.getRol()) // Asumiendo que tienes un getRol()
                .build();
    }
}