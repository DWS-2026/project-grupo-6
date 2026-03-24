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
        
        Principal principal = request.getUserPrincipal();

        if (principal != null) {
            // ¡El usuario ha iniciado sesión!
            model.addAttribute("loggedUser", true);
            
            // Buscamos su nombre de usuario en la BD (usando su email)
            String email = principal.getName();
            User user = userService.findByEmail(email).orElse(null);
            
            if (user != null) {
                // Pasamos el username para que Mustache pinte {{username}}
                model.addAttribute("username", user.getUsername()); 
                
                // EXTRA: Si tienes un método para contar su carrito, puedes meterlo aquí también
                model.addAttribute("cartCount", cartService.getCartItemCount(user.getId())); 
            }
        } else {
            // Usuario anónimo (visitante)
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