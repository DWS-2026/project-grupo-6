package com.example.projectgrupo6.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class WebController {

    @GetMapping("/") 
    public String greeting(Model model){
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
