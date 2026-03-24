package com.example.projectgrupo6.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        
        // 1. SAVIOUR: ALWAYS defect values at the beginning
        // Mustache will never be empty here
        model.addAttribute("cartCount", 0);
        model.addAttribute("loggedUser", false); // Mustache prefers false booleans to null for blocks {{# ...}}

        try {
            Long userId = userService.getCurrentUserId(session);
            
            // Check Id not null
            if (userId != null) {
                Optional<User> userOpt = userService.getById(userId);
                
                if (userOpt.isPresent()) {
                    // 2. If user exists, overwrite values by default
                    model.addAttribute("loggedUser", userOpt.get());
                    int cartCount = cartService.getCartTotalItems(userId);
                    model.addAttribute("cartCount", cartCount);
                }
            }
        } catch (Exception e) {
            // If it jumps to any error we don't care, in line 1 we put cartCount to 0
            // Optional: print error to debug
            // System.err.println("Error al cargar atributos globales: " + e.getMessage());
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