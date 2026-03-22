package com.example.projectgrupo6.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	@Autowired
    public RepositoryUserDetailsService userDetailService;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailService);
		authProvider.setPasswordEncoder(passwordEncoder());

		return authProvider;
	}

	@Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        
        http.authenticationProvider(authenticationProvider());
        
        http
            .authorizeHttpRequests(authorize -> authorize
                    // Static resources and error page should be public
                    .requestMatchers(   "/css/**", 
                                                    "/webfonts/**", 
                                                    "/error"
                                                ).permitAll()
                    //imagepaths should be public too, but we have to allow the dynamic ones with wildcards
                    .requestMatchers("/product/*/image/*", "/*/image").permitAll() // Rutas de fotos BD
                    
                    // PUBLIC PAGES
                    .requestMatchers("/").permitAll()
                    .requestMatchers("/user/new").permitAll()
                    .requestMatchers("/shop/**").permitAll() // <-- Asegúrate de que la tienda es pública
                    .requestMatchers("/product/*/image/*").permitAll() // <-- Tus rutas dinámicas de fotos
                    
                    // PRIVATE PAGES (Ajústalo a tu proyecto real, veo cosas de "books" del profe)
                    .requestMatchers("/product/add").hasAnyRole("ADMIN")
                    .requestMatchers("/admin/**").hasAnyRole("ADMIN")
                    .requestMatchers("/cart/**").hasAnyRole("USER", "ADMIN")
                    .requestMatchers("/user/profile").hasAnyRole("USER", "ADMIN")
                    
                    // IMPORTANTE: El resto de peticiones que no coincidan arriba, exigimos login
                    .anyRequest().authenticated() 
            )
            .formLogin(formLogin -> formLogin
                    .loginPage("/user/login")
                    .usernameParameter("email")
                    .failureUrl("/user/loginerror")
                    .defaultSuccessUrl("/user/profile")
                    .permitAll()
            )
            .logout(logout -> logout
                    .logoutUrl("/user/logout") // Igual aquí, suele ser /user/logout
                    .logoutSuccessUrl("/")
                    .permitAll()
            );
        
        // Disable CSRF at the moment
        http.csrf(csrf -> csrf.disable());
        
        return http.build();
    }

}
