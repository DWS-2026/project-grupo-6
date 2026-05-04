package com.example.projectgrupo6.controllers.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.projectgrupo6.domain.User;
import com.example.projectgrupo6.services.UserService;
import com.example.projectgrupo6.services.CartService;

import jakarta.servlet.http.HttpServletRequest;

import java.security.Principal;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    private void populateGlobalAttributes(Model model, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            model.addAttribute("loggedUser", true);
            String email = principal.getName();
            User user = userService.findByEmail(email).orElse(null);
            
            if (user != null) {
                model.addAttribute("username", user.getUsername()); 
                model.addAttribute("cartCount", cartService.getCartItemCount(user.getId())); 
            } else {
                model.addAttribute("loggedUser", false);
                model.addAttribute("cartCount", 0);
            }
        } else {
            model.addAttribute("loggedUser", false);
            model.addAttribute("cartCount", 0);
        }
    }

    @ModelAttribute
    public void addGlobalAttributes(Model model, HttpServletRequest request) {
        populateGlobalAttributes(model, request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Object handleValidation(IllegalArgumentException ex, Model model, HttpServletRequest request) {
        
        if (request.getRequestURI().startsWith("/api")) {
            return ResponseEntity.badRequest().body(new ErrorDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Error de Validación",
                ex.getMessage()
            ));
        }

        populateGlobalAttributes(model, request);
        model.addAttribute("status", HttpStatus.BAD_REQUEST.value()); 
        model.addAttribute("error", "Error de Validación");
        model.addAttribute("message", ex.getMessage()); 
        return "error"; 
    }

    @ExceptionHandler(Exception.class)
    public Object handleAll(Exception ex, Model model, HttpServletRequest request) {
        
        if (request.getRequestURI().startsWith("/api")) {
            return ResponseEntity.status(500).body(new ErrorDTO(
                500,
                "Internal Server Error",
                "Algo salió mal en nuestros servidores."
            ));
        }

        populateGlobalAttributes(model, request);
        model.addAttribute("status", 500);
        model.addAttribute("error", "Internal Server Error");
        model.addAttribute("message", "Algo salió mal en nuestros servidores.");
        return "error";
    }

    public record ErrorDTO(int status, String error, String message) {}
}