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
                    .requestMatchers("/product/*/image/*", "/*/image", "/*/image/*").permitAll() // Routes of BD images
                    
                    // PUBLIC PAGES
                    .requestMatchers("/").permitAll()
                    .requestMatchers("/user/new").permitAll()
					.requestMatchers("/user/*/image").permitAll() //User image picture should be public too
                    .requestMatchers("/shop/**").permitAll() // <-- Make sure that the store is public
                    
                    // PRIVATE PAGES
                    .requestMatchers("/product/add").hasAnyRole("ADMIN")
                    .requestMatchers("/shop/*/comment/**", "/add-to-cart", "/cart/**", "/cart/add/**", "/cart/remove/**").authenticated()
                    .requestMatchers("/admin/**").hasAnyRole("ADMIN")
                    .requestMatchers("/cart/**").hasAnyRole("USER", "ADMIN")
					.requestMatchers("/user/**").hasAnyRole("USER", "ADMIN") // <-- The rest of user pages
                    
                    // IMPORTANT: The rest of petitions need login
                    .anyRequest().authenticated() 
            )
            .formLogin(formLogin -> formLogin
                    .loginPage("/login")
                    .usernameParameter("email")
                    .failureUrl("/loginerror")
                    .defaultSuccessUrl("/user/profile")
                    .permitAll()
            )
            .logout(logout -> logout
                    .logoutUrl("/user/logout") // Same here, is usually /user/logout
                    .logoutSuccessUrl("/")
                    .permitAll()
            );
        
        // Disable CSRF at the moment
        http.csrf(csrf -> csrf.disable());
        
        return http.build();
    }

}
