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
        
        // 1. EL SALVAVIDAS: Ponemos los valores por defecto SIEMPRE al principio.
        // Así, pase lo que pase, Mustache nunca se quedará en blanco.
        model.addAttribute("cartCount", 0);
        model.addAttribute("loggedUser", false); // Mustache prefiere booleanos falsos a nulos para los bloques {{#...}}

        try {
            Long userId = userService.getCurrentUserId(session);
            
            // Comprobamos que el ID no sea null antes de buscar en la BD
            if (userId != null) {
                Optional<User> userOpt = userService.getById(userId);
                
                if (userOpt.isPresent()) {
                    // 2. Si el usuario existe de verdad, SOBREESCRIBIMOS los valores por defecto
                    model.addAttribute("loggedUser", userOpt.get());
                    int cartCount = cartService.getCartTotalItems(userId);
                    model.addAttribute("cartCount", cartCount);
                }
            }
        } catch (Exception e) {
            // Si salta cualquier error (ya sea RuntimeException o cualquier otro), 
            // no nos importa, porque en la línea 1 ya le pusimos el cartCount a 0.
            // Opcional: imprimir el error para depurar si falla algo raro.
            // System.err.println("Error al cargar atributos globales: " + e.getMessage());
        }
    }

    @ExceptionHandler(Exception.class)
    public String handleError() {
        return "error";
    }
    @ExceptionHandler(MultipartException.class)
    public String handleMultipartException(MultipartException exc, RedirectAttributes redirectAttributes) {
        
        // Le mandamos el mensaje rojo al formulario
        redirectAttributes.addFlashAttribute("errorMessage", "Error: The uploaded files are too large or invalid. Maximum 15MB per file.");
        
        // Lo devolvemos a la página de añadir producto
        return "redirect:/product/add"; 
    }
}