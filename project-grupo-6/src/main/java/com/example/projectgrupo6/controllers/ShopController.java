package com.example.projectgrupo6.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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

    private Long getCurrentUserId() {
        return 1L;
    }
    
    @GetMapping("/shop") 
    public String showshop(Model model){
        
        List<Product> products = productService.getAllProducts();
        
        model.addAttribute("products", products);
        return "shop";
    }
    
    @GetMapping("/shopping-cart") 
    public String showshoppingcart(Model model){
        Long userId = getCurrentUserId(); // Implementa este método para obtener el ID del usuario actual
        List<CartItem> cartItems = cartService.getCartItems(userId);
        double total = cartService.getCartTotal(userId);
        int totalItems = cartService.getCartTotalItems(userId);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", total);
        model.addAttribute("totalItems", totalItems);
        return "shopping-cart";

    }
    
    @GetMapping("/shop-single/{id}")
    public String showProduct(@PathVariable Long id, Model model) {
        
        Optional<Product> productOpt = productService.getById(id);
        if (!productOpt.isEmpty()) {
            model.addAttribute("product", productOpt.get());
            return "shop-single";
        } else {
            return "redirect:/shop";
        }
  
    }
    @PostMapping("/add-to-cart")
    public String addToCart(@RequestParam Long productId,
                            @RequestParam(defaultValue = "1") int quantity,
                            RedirectAttributes redirectAttributes) {
        Long userId = getCurrentUserId();
        try {
            cartService.addProductToCart(userId, productId, quantity);
            redirectAttributes.addFlashAttribute("successMessage", "Product added to cart.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        return "redirect:/shopping-cart";
    }
    
    

}
