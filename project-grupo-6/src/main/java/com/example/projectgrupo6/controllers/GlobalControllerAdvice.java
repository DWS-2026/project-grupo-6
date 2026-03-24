package com.example.projectgrupo6.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.projectgrupo6.domain.User;
import com.example.projectgrupo6.services.UserService;
import com.example.projectgrupo6.services.CartService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;
import java.util.Optional;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @ModelAttribute
    public void addGlobalAttributes(Model model, HttpServletRequest request) {
        // 1. Valores por defecto (Reset)
        model.addAttribute("loggedUser", false);
        model.addAttribute("cartCount", 0);

        // 2. Obtain the user from Spring Security
        Principal principal = request.getUserPrincipal();

        if (principal != null) {
            // The principal.getName() gives us the "username" used to log in
            String username = principal.getName();
            
            // Buscamos el objeto User completo para que Mustache lo use
            Optional<User> userOpt = userService.findByUsername(username);

            if (userOpt.isPresent()) {
                User user = userOpt.get();
                
                // INJECT THE REAL OBJECT
                model.addAttribute("loggedUser", user);
                
                // LOAD THE CART USING THE FOUND ID OF USER
                int items = cartService.getCartTotalItems(user.getId());
                model.addAttribute("cartCount", items);
                
                // Log zto Fedora (search your terminal to refresh the page)
                // System.out.println("DEBUG: Header cargado para: " + username);
            }
        }
    }

//    @ExceptionHandler(Exception.class)
//    public String handleError() {
//        return "error";
//    }
//    @ExceptionHandler(MultipartException.class)
//    public String handleMultipartException(MultipartException exc, RedirectAttributes redirectAttributes) {
//
//        // Broken message to form
//        redirectAttributes.addFlashAttribute("errorMessage", "Error: The uploaded files are too large or invalid. Maximum 15MB per file.");
//
//        // Return to addProduct page
//        return "redirect:/product/add";
//    }
}