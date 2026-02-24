package com.example.projectgrupo6.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.projectgrupo6.domain.User;
import com.example.projectgrupo6.services.UserService;
import com.example.projectgrupo6.services.CartService;

import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    // Se ejecuta ANTES de que se devuelva cualquier vista (HTML)
    @ModelAttribute
    public void addGlobalAttributes(Model model, HttpSession session) {
        try {
            // Tu método lanza RuntimeException si es null, ¡perfecto para el try-catch!
            Long userId = userService.getCurrentUserId(session);
            Optional<User> userOpt = userService.getById(userId);
            
            if (userOpt.isPresent()) {
                // Si existe, lo pasamos a Mustache bajo el nombre "loggedUser"
                model.addAttribute("loggedUser", userOpt.get());
                
                // Aprovechamos y pasamos también el carrito a todas las páginas
                int cartCount = cartService.getCartTotalItems(userId);
                model.addAttribute("cartCount", cartCount);
            }
        } catch (RuntimeException e) {
            // Si salta el error de tu UserService, es un visitante anónimo
            model.addAttribute("cartCount", 0);
        }
    }
}