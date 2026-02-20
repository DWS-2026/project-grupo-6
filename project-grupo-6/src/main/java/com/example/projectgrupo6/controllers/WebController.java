package com.example.projectgrupo6.controllers;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.h2.engine.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.projectgrupo6.domain.CartItem;
import com.example.projectgrupo6.domain.Product;
import com.example.projectgrupo6.services.CartService;
import com.example.projectgrupo6.services.ProductService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class WebController {
    //add HttpObject for sessions

    @Autowired
    private ProductService productService;

    @Autowired
    private CartService cartService;
    @GetMapping("/") 
    public String greeting(Model model,HttpSession session){
        
        List<Product> products = productService.getAllProducts();
        
        if (!products.isEmpty()) {
            model.addAttribute("firstProduct", products.get(0));
            if (products.size() > 1) {
                model.addAttribute("restProducts", products.subList(1, Math.min(3, products.size())));
            }
        }

        List<Product> randomFeatured = productService.getThreeRandomProducts();
        model.addAttribute("featuredProducts", randomFeatured);
        try {
        Long userId = getCurrentUserId(session);
        int cartCount = cartService.getCartTotalItems(userId);
        model.addAttribute("cartCount", cartCount);
        } catch (RuntimeException e) {
        // Usuario no autenticado, no mostrar contador
        model.addAttribute("cartCount", 0);
        }

        return "index";
    }


    @GetMapping("/login") 
    public String showlogin(Model model){
        return "login";
    }

    @GetMapping("/user-form") 
    public String showuserform(Model model){
        return "user-form";
    }
    private Long getCurrentUserId(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if(user ==null){
            throw new RuntimeException("Unaunthenticated user");
        }
        return (long) user.getId();
    }
    @GetMapping("/cart")
    public String showCart(HttpSession session, Model model) {
        Long userId = getCurrentUserId(session);
        List<CartItem> cartItems = cartService.getCartItems(userId);
        double total = cartService.getCartTotal(userId);
        int totalItems = cartService.getCartTotalItems(userId);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", total);
        model.addAttribute("totalItems", totalItems);
        return "cart";
    }
    @PostMapping("/cart/add/{productId}")
    public String addToCart(@PathVariable Long productId, @RequestParam(defaultValue = "1") int quantity, HttpSession session) {
        Long userId = getCurrentUserId(session);
        cartService.addProductToCart(userId, productId, quantity);
        return "redirect:/cart";
    }
    @PostMapping("/cart/remove/{productId}")
    public String removeFromCart(@PathVariable Long productId, HttpSession session) {
        Long userId = getCurrentUserId(session);
        cartService.removeProductFromCart(userId, productId);
        return "redirect:/cart";
    }
    @PostMapping("/cart/update/{productId}")
    public String updateCart(@PathVariable Long productId, @RequestParam int quantity, HttpSession session) {
        Long userId = getCurrentUserId(session);
        cartService.updateProductQuantity(userId, productId, quantity);
        return "redirect:/cart";
    }
    @PostMapping("/cart/clear")
    public String clearCart(HttpSession session) {
        Long userId = getCurrentUserId(session);
        cartService.clearCart(userId);
        return "redirect:/cart";
    }
    
}
