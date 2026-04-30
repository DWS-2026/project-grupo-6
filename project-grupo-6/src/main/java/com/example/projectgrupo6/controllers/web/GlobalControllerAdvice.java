package com.example.projectgrupo6.controllers.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
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

    @ModelAttribute
    public void addGlobalAttributes(Model model, HttpServletRequest request) {
        
        Principal principal = request.getUserPrincipal();

        if (principal != null) {
            // Logged user!
            model.addAttribute("loggedUser", true);
            
            // Search by username in DB (using its email)
            String email = principal.getName();
            User user = userService.findByEmail(email).orElse(null);
            
            if (user != null) {
                // Send username to Mustache {{username}}
                model.addAttribute("username", user.getUsername()); 
                
                // EXTRA: Method to count its CartItems
                model.addAttribute("cartCount", cartService.getCartItemCount(user.getId())); 
            } else {
                //IMPORTANT: consistent fallback
                model.addAttribute("loggedUser", false);
                model.addAttribute("cartCount", 0);
            }
        } else {
            // Anonymous user (visitor)
            model.addAttribute("loggedUser", false);
            model.addAttribute("cartCount", 0);
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