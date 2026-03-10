package com.example.projectgrupo6.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
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

    //Executes BEFORE rendering any view (HTML)
    @ModelAttribute
    public void addGlobalAttributes(Model model, HttpSession session) {
        try {
            // Method throws RuntimeException if null
            Long userId = userService.getCurrentUserId(session);
            Optional<User> userOpt = userService.getById(userId);
            
            if (userOpt.isPresent()) {
                // If exists, send to Mustache with the name "loggedUser"
                model.addAttribute("loggedUser", userOpt.get());
                
                // We send the cart all of its pages
                int cartCount = cartService.getCartTotalItems(userId);
                model.addAttribute("cartCount", cartCount);
            } else {
                model.addAttribute("loggedUser", null); //for Mustache
                model.addAttribute("cartCount", 0);
            }
        } catch (RuntimeException e) {
            // If error in UserService, anonymous session
            model.addAttribute("cartCount", 0);
        }
    }

    @ExceptionHandler(Exception.class)
    public String handleError() {
        return "error";
    }
}