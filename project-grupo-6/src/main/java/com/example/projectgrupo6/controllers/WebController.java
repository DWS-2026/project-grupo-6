package com.example.projectgrupo6.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.projectgrupo6.domain.Product;
import com.example.projectgrupo6.services.ProductService;

@Controller
public class WebController {

    @Autowired
    private ProductService productService;

    @GetMapping("/") 
    public String greeting(Model model){
        List<Product> products = productService.getAllProducts();
        
        if (!products.isEmpty()) {
            model.addAttribute("firstProduct", products.get(0));
            if (products.size() > 1) {
                model.addAttribute("restProducts", products.subList(1, Math.min(3, products.size())));
            }
        }

        List<Product> randomFeatured = productService.getThreeRandomProducts();
        model.addAttribute("featuredProducts", randomFeatured);

        return "index";
    }

    @GetMapping("/shop") 
    public String showshop(Model model){
        return "shop";
    }
    @GetMapping("/shopping-cart") 
    public String showshoppingcart(Model model){
        return "shopping-cart";
    }
    @GetMapping("/shop-single")
    public String showProduct(Model model) {
        return "shop-single";
    }
    
    @GetMapping("/user-form") 
    public String showuserform(Model model){
        return "user-form";
    }

    @GetMapping("/login") 
    public String showlogin(Model model){
        return "login";
    }
}
