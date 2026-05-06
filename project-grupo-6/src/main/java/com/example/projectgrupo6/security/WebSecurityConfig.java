package com.example.projectgrupo6.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.projectgrupo6.security.jwt.JwtRequestFilter;
import com.example.projectgrupo6.security.jwt.JwtTokenProvider;
import com.example.projectgrupo6.security.jwt.UnauthorizedHandlerJwt;

import org.springframework.http.HttpMethod;


@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
	private JwtTokenProvider jwtTokenProvider;

	@Autowired
    public RepositoryUserDetailsService userDetailService;

    @Autowired
  	private UnauthorizedHandlerJwt unauthorizedHandlerJwt;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

    @Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailService);
		authProvider.setPasswordEncoder(passwordEncoder());

		return authProvider;
	}

    @Bean
	@Order(1)
	public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
		
		http.authenticationProvider(authenticationProvider());
		
		http
			.securityMatcher("/api/**")
			.exceptionHandling(handling -> handling.authenticationEntryPoint(unauthorizedHandlerJwt));
		
		http
			.authorizeHttpRequests(authorize -> authorize
                    // Public API endpoints
                    .requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/register", "/api/auth/refresh").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/products/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/images/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/comments/**").permitAll()

                    // ==========================================
                    // ADMIN API ENDPOINTS 
                    // ==========================================
                    // - Products: Create, Update and Delete
                    .requestMatchers(HttpMethod.POST, "/api/v1/products/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/api/v1/products/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/products/**").hasRole("ADMIN")
                    
                    // - Users: Get all and Create with admin role
                    .requestMatchers(HttpMethod.GET, "/api/v1/users/").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/api/v1/users/").hasRole("ADMIN")
                    
                    // - Carts: Get all
                    .requestMatchers(HttpMethod.GET, "/api/v1/carts/").hasRole("ADMIN")
                    
                    // - Orders: Get all, Update and Add file
                    .requestMatchers(HttpMethod.GET, "/api/v1/orders/").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/api/v1/orders/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/api/v1/orders/*/file").hasRole("ADMIN")

                    // ==========================================
                    // Rest of authenticated API endpoints (for both USER and ADMIN)
                    // ==========================================
                    .anyRequest().authenticated()
			);
	
        // Disable Form login Authentication
        http.formLogin(formLogin -> formLogin.disable());

        // Disable CSRF protection (it is difficult to implement in REST APIs)
        http.csrf(csrf -> csrf.disable());

        // Disable Basic Authentication
        http.httpBasic(httpBasic -> httpBasic.disable());

        // Stateless session
        http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		// Add JWT Token filter
		http.addFilterBefore(new JwtRequestFilter(userDetailService, jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
    @Order(2)
    public SecurityFilterChain webfilterChain(HttpSecurity http) throws Exception {
        
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
					.requestMatchers("/api/**").permitAll()
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
        //http.csrf(csrf -> csrf.disable());
        
        return http.build();
    }

}
