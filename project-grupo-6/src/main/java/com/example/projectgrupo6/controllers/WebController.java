package com.example.projectgrupo6.controllers;

import java.util.List;

import com.example.projectgrupo6.services.UserService;
import jakarta.servlet.http.HttpSession;

import com.example.projectgrupo6.services.ImageService;
import com.example.projectgrupo6.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.projectgrupo6.domain.CartItem;
import com.example.projectgrupo6.domain.Product;
import com.example.projectgrupo6.services.CartService;
import com.example.projectgrupo6.services.ProductService;


@Controller
public class WebController {
    //add HttpObject for sessions

    @Autowired
    private ProductService productService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @GetMapping("/") 
    public String greeting(Model model, @ModelAttribute("logoutMessage") String logoutMessage, HttpSession session){
        
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
        Long userId = userService.getCurrentUserId(session);
        int cartCount = cartService.getCartTotalItems(userId);
        model.addAttribute("cartCount", cartCount);
        } catch (RuntimeException e) {
        // Not authenticated user, don't show counter
        model.addAttribute("cartCount", 0);
        }

        return "index";
    }

    /*
    @GetMapping("/login") 
    public String showlogin(Model model){
        return "login";
    }
    */

    //Already in User Controller:
    @GetMapping("/user-form") 
    public String showuserform(Model model){
        return "user-form";
    }


    @GetMapping("/cart")
public String showCart(HttpSession session, Model model) {
    try {
        Long userId = userService.getCurrentUserId(session);
        List<CartItem> cartItems = cartService.getCartItems(userId);
        double total = cartService.getCartTotal(userId);
        int totalItems = cartService.getCartTotalItems(userId);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", total);
        model.addAttribute("totalItems", totalItems);
        return "shopping-cart";
    } catch (RuntimeException e) {
        return "redirect:/user/login";
    }
}

    @PostMapping("/cart/add/{productId}")
    public String addToCart(@PathVariable Long productId, @RequestParam(defaultValue = "1") int quantity, HttpSession session) {
        try {
            Long userId = userService.getCurrentUserId(session);
            cartService.addProductToCart(userId, productId, quantity);
            return "redirect:/cart";
        } catch (RuntimeException e) {
            return "redirect:/user/login";
        }
    }

    @PostMapping("/cart/remove/{productId}")
    public String removeFromCart(@PathVariable Long productId, HttpSession session) {
        try{
            Long userId = userService.getCurrentUserId(session);
            cartService.removeProductFromCart(userId, productId);
            return "redirect:/cart";
        }catch(RuntimeException e){
            return "redirect:/user/login";
        }
    }

    @PostMapping("/cart/update/{productId}")
    public String updateCart(@PathVariable Long productId, @RequestParam int quantity, HttpSession session) {
        try{
            Long userId = userService.getCurrentUserId(session);
            cartService.updateProductQuantity(userId, productId, quantity);
            return "redirect:/cart";
        }catch(RuntimeException e){
            return "redirect:/user/login";
        }
        
    }

    @PostMapping("/cart/clear")
    public String clearCart(HttpSession session) {
        try {
            Long userId = userService.getCurrentUserId(session);
            cartService.clearCart(userId);
            return "redirect:/cart";
        } catch (RuntimeException e) {
            return "redirect:/user/login";
        }
    }
}
