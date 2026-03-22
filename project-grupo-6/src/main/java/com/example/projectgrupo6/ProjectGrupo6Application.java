package com.example.projectgrupo6;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@SpringBootApplication
public class ProjectGrupo6Application {

    public static void main(String[] args) {
        SpringApplication.run(ProjectGrupo6Application.class, args);
    }

   /*  @Bean
    public ServletRegistrationBean<JakartaWebServlet> h2servletRegistration() {
        ServletRegistrationBean<JakartaWebServlet> registration = 
            new ServletRegistrationBean<>(new JakartaWebServlet());
        registration.addUrlMappings("/h2-console/*");
        return registration;
    }
        */
    

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
