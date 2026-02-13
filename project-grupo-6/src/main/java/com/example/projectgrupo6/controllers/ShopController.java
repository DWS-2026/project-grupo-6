package com.example.projectgrupo6.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.projectgrupo6.domain.Product;
import com.example.projectgrupo6.services.ProductService;

@Controller
public class ShopController {
    //add HttpObject for sessions

    @Autowired
    private ProductService productService;
    
    @GetMapping("/shop") 
    public String showshop(Model model){
        
        List<Product> products = productService.getAllProducts();
        
        model.addAttribute("products", products);
        return "shop";
    }
    
    @GetMapping("/shopping-cart") 
    public String showshoppingcart(Model model){

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
    

}
