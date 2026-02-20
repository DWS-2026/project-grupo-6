package com.example.projectgrupo6.controllers;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.h2.engine.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.micrometer.observation.autoconfigure.ObservationProperties.Http;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.projectgrupo6.domain.Product;
import com.example.projectgrupo6.domain.CartItem;
import com.example.projectgrupo6.services.CartService;
import com.example.projectgrupo6.services.ProductService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class ShopController {
    //add HttpObject for sessions

    @Autowired
    private ProductService productService;
    
    @Autowired
    private CartService cartService;

    private Long getCurrentUserId(HttpSession session) {
         User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new RuntimeException("Usuario no autenticado");
        }
        return (long) user.getId();
    }
    
    
    @GetMapping("/shop") 
    public String showshop(Model model,HttpSession session){
        
        List<Product> products = productService.getAllProducts();
        
        model.addAttribute("products", products);
        try {
            Long userId = getCurrentUserId(session);
            int cartCount = cartService.getCartTotalItems(userId);
            model.addAttribute("cartCount", cartCount);
        } catch (RuntimeException e) {
            model.addAttribute("cartCount", 0);
        }
        return "shop";
    }
    
    @GetMapping("/shopping-cart") 
    public String showshoppingcart(Model model,HttpSession session){
        Long userId = getCurrentUserId(session); // Implementa este método para obtener el ID del usuario actual
        List<CartItem> cartItems = cartService.getCartItems(userId);
        double total = cartService.getCartTotal(userId);
        int totalItems = cartService.getCartTotalItems(userId);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", total);
        model.addAttribute("totalItems", totalItems);
        return "shopping-cart";

    }
    
    @GetMapping("/shop-single/{id}")
    public String showProduct(@PathVariable Long id, Model model, HttpSession session) {
        
        Optional<Product> productOpt = productService.getById(id);
        if (!productOpt.isPresent()) {
            model.addAttribute("product", productOpt.get());
             try {
                Long userId = getCurrentUserId(session);
                int cartCount = cartService.getCartTotalItems(userId);
                model.addAttribute("cartCount", cartCount);
            } catch (RuntimeException e) {
                model.addAttribute("cartCount", 0);
            }
            return "shop-single";
        } else {
            return "redirect:/shop";
        }
  
    }
    @PostMapping("/add-to-cart")
    public String addToCart(@RequestParam Long productId,
                            @RequestParam(defaultValue = "1") int quantity,
                            RedirectAttributes redirectAttributes,HttpSession session) {
        try {
            Long userId = getCurrentUserId(session);
            cartService.addProductToCart(userId, productId, quantity);
            redirectAttributes.addFlashAttribute("successMessage", "Producto añadido al carrito.");
        } catch (RuntimeException e) {
            // Si el usuario no está autenticado, redirigir al login
            if (e.getMessage().equals("Usuario no autenticado")) {
                return "redirect:/login";
            }
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        // Redirigir a la página desde donde se hizo la petición (o al carrito)
        String referer = session.getAttribute("previousPage") != null ? 
                         session.getAttribute("previousPage").toString() : "/shopping-cart";
        return "redirect:" + referer;
    }
    
    
    

}
