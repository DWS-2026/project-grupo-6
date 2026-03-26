package com.example.projectgrupo6.controllers;

import java.io.IOException;
import java.util.List;

import javax.sql.rowset.serial.SerialBlob;

import com.example.projectgrupo6.services.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import com.example.projectgrupo6.services.ImageService;
import com.example.projectgrupo6.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.projectgrupo6.domain.CartItem;
import com.example.projectgrupo6.domain.Image;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

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

    @GetMapping("/login")
    public String showlogin(HttpServletRequest request ,Model model){
        
        if(request.getUserPrincipal() != null){
            return "redirect:/user/profile";
        }

        return "login";
    }
        
    @GetMapping("/loginerror")
    public String loginError() {
        return "loginerror";
    }

    @GetMapping ("/user/new")
    public String register (Model model){
        model.addAttribute("user", new User());
        model.addAttribute("edit", false);

        return "user-form";
    }
        
    @PostMapping("/user/new")
    public ResponseEntity<String> registerSubmit(@ModelAttribute("user") User user, 
                                                @RequestParam("confirmPassword") String confirmPassword, 
                                                @RequestParam(value = "image", required = false) MultipartFile image) throws Exception {

        //if (userService.findByEmail(user.getEmail()).isPresent() || userService.findByUsername(user.getUsername()).isPresent()) {
        // An user can be regsitred with same username? In case not, uncomment the line above and add the username check
        if (userService.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User with those credentials already exists");
        }

        if (!user.getEncodedPassword().equals(confirmPassword)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Passwords don't match");
        }

        user.setEncodedPassword(passwordEncoder.encode(user.getEncodedPassword()));

        if (image != null && !image.isEmpty()) {
            try {
                Image saved = imageService.createImage(image);
                user.setProfileImage(new SerialBlob(saved.getImageFile()));
            } catch (IOException e) {
                throw new RuntimeException("Error processing uploaded image", e);
            }
        } else {
            user.setProfileImage(imageService.loadImage("defaultUserImage.png"));
        }

        user.setRol("USER"); 
        userService.save(user);

        return ResponseEntity.ok("/login");
    }

    
}
