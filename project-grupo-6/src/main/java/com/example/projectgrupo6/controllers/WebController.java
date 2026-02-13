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
    //add HttpObject for sessions

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


    @GetMapping("/login") 
    public String showlogin(Model model){
        return "login";
    }

    @GetMapping("/user-form") 
    public String showuserform(Model model){
        return "user-form";
    }
}
