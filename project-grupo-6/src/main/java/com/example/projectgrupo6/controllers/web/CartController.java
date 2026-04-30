package com.example.projectgrupo6.controllers.web;
import com.example.projectgrupo6.services.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.projectgrupo6.domain.CartItem;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import com.example.projectgrupo6.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


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

    @Autowired
    private ValidationService validationService;

    //change
    @GetMapping("")
    public String showCart(HttpServletRequest request, Model model) {
        User user = userService.getSessionUser(request);
        if (user == null) return "redirect:/login";

        Long userId = user.getId();
        List<CartItem> cartItems = cartService.getCartItems(userId);
        double total = cartService.getCartTotal(userId);
        int totalItems = cartService.getCartTotalItems(userId);
        
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", total);
        model.addAttribute("totalItems", totalItems);
        
        return "shopping-cart";
    }

    //change
    @PostMapping("/add/{productId}")
    public String addToCart(@PathVariable Long productId, @RequestParam(defaultValue = "1") int quantity, HttpServletRequest request) {
        User user = userService.getSessionUser(request);
        if (user == null) return "redirect:/login";

        if(!validationService.isValidQuantity(quantity)) quantity = 0;
        cartService.addProductToCart(user.getId(), productId, quantity);
        return "redirect:/cart";
    }

    //change
    @PostMapping("/remove/{productId}")
    public String removeFromCart(@PathVariable Long productId, HttpServletRequest request) {
        User user = userService.getSessionUser(request);
        if (user == null) return "redirect:/login";

        cartService.removeProductFromCart(user.getId(), productId);
        return "redirect:/cart";
    }

    //change
    @PostMapping("/update/{productId}")
    public String updateCart(@PathVariable Long productId, @RequestParam int quantity, HttpServletRequest request) {
        User user = userService.getSessionUser(request);
        if (user == null) return "redirect:/login";

        if(!validationService.isValidQuantity(quantity)) quantity = 0;
        cartService.updateProductQuantity(user.getId(), productId, quantity);
        return "redirect:/cart";
    }

    //change
    @PostMapping("/clear")
    public String clearCart(HttpServletRequest request) {
        User user = userService.getSessionUser(request);
        if (user == null) return "redirect:/login";

        cartService.clearCart(user.getId());
        return "redirect:/cart";
    }

    //change
    @PostMapping("/checkout")
    public String checkout(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        User user = userService.getSessionUser(request);
        if (user == null) return "redirect:/login";

        Long userId = user.getId();
        List<CartItem> cartItems = cartService.getCartItems(userId);
        
        if (cartItems.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Your cart is empty. Add some products first!");
            return "redirect:/cart"; 
        }

        double total = cartService.getCartTotal(userId);

        orderService.createOrderFromCart(user, cartItems, total);
        cartService.clearCart(userId);

        redirectAttributes.addFlashAttribute("successMessage", "Purchase completed successfully! Thank you for your order.");
        
        // ¡Watch out! Make sure that the route exists in UserController or wherever you manage the orders
        return "redirect:/user/orders";
    }

}
