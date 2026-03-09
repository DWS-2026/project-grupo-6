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
    public String greeting(Model model, HttpSession session){
        
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

}
