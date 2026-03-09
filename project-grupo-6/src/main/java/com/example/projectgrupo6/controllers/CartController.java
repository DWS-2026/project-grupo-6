package com.example.projectgrupo6.controllers;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.projectgrupo6.domain.CartItem;
import java.util.List;

import com.example.projectgrupo6.services.UserService;
import jakarta.servlet.http.HttpSession;

import com.example.projectgrupo6.services.ImageService;
import com.example.projectgrupo6.services.OrderService;
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
@RequestMapping("/cart")    
public class CartController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @GetMapping("")
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

    @PostMapping("/add/{productId}")
    public String addToCart(@PathVariable Long productId, @RequestParam(defaultValue = "1") int quantity, HttpSession session) {
        try {
            Long userId = userService.getCurrentUserId(session);
            cartService.addProductToCart(userId, productId, quantity);
            return "redirect:/cart";
        } catch (RuntimeException e) {
            return "redirect:/user/login";
        }
    }

    @PostMapping("/remove/{productId}")
    public String removeFromCart(@PathVariable Long productId, HttpSession session) {
        try{
            Long userId = userService.getCurrentUserId(session);
            cartService.removeProductFromCart(userId, productId);
            return "redirect:/cart";
        }catch(RuntimeException e){
            return "redirect:/user/login";
        }
    }

    @PostMapping("/update/{productId}")
    public String updateCart(@PathVariable Long productId, @RequestParam int quantity, HttpSession session) {
        try{
            Long userId = userService.getCurrentUserId(session);
            cartService.updateProductQuantity(userId, productId, quantity);
            return "redirect:/cart";
        }catch(RuntimeException e){
            return "redirect:/user/login";
        }
        
    }

    @PostMapping("/clear")
    public String clearCart(HttpSession session) {
        try {
            Long userId = userService.getCurrentUserId(session);
            cartService.clearCart(userId);
            return "redirect:/cart";
        } catch (RuntimeException e) {
            return "redirect:/user/login";
        }
    }

    @PostMapping("/checkout")
    public String checkout(HttpSession session, RedirectAttributes redirectAttributes) {
        try {

            Long userId = userService.getCurrentUserId(session);
            User user = userService.getById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<CartItem> cartItems = cartService.getCartItems(userId);
            
            if (cartItems.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Your cart is empty. Add some products first!");
                return "redirect:/cart"; 
            }

            double total = cartService.getCartTotal(userId);

            orderService.createOrderFromCart(user, cartItems, total);

            cartService.clearCart(userId);

            redirectAttributes.addFlashAttribute("successMessage", "Purchase completed successfully! Thank you for your order.");
            return "redirect:/user/orders"; 

        } catch (RuntimeException e) {
            return "redirect:/user/login";
        }
    }

}
