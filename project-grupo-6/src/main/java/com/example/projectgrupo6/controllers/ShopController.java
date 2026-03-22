package com.example.projectgrupo6.controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.example.projectgrupo6.services.ImageService;
import jakarta.servlet.http.HttpSession;

import com.example.projectgrupo6.services.UserService;
import com.example.projectgrupo6.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.micrometer.observation.autoconfigure.ObservationProperties.Http;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.projectgrupo6.domain.Product;
import com.example.projectgrupo6.domain.CartItem;
import com.example.projectgrupo6.domain.Comment;
import com.example.projectgrupo6.services.CartService;
import com.example.projectgrupo6.services.CommentService;
import com.example.projectgrupo6.services.ProductService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.core.io.Resource;
import java.sql.Blob;
import org.springframework.http.MediaType;


@Controller
public class ShopController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private CartService cartService;


    private Long getCurrentUserId(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new RuntimeException("Usuario no autenticado");
        }
        return user.getId();
    }

    @GetMapping("/shop") 
    public String showshop(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "shop";
    }
    
    
    @GetMapping("/shop/{id}")
    public String showProduct(@PathVariable Long id, Model model, HttpSession session) {
        Optional<Product> productOpt = productService.getById(id);

        if (productOpt.isPresent()) {
            model.addAttribute("product", productOpt.get());

            User sessionUser = (User) session.getAttribute("user");
            Long userId = (sessionUser != null) ? sessionUser.getId() : null;

            List<Map<String, Object>> commentsView = commentService.getCommentsForProductView(id, userId);
            model.addAttribute("productComments", commentsView);

            return "shop-single";  
        } else {
            return "redirect:/shop";
        }
    }
    @GetMapping("/product/{id}/image/{index}")
    public ResponseEntity<Resource> getProductImageByIndex(@PathVariable Long id, @PathVariable int index) throws SQLException {
        Optional<Product> productOpt = productService.getById(id);

        if (productOpt.isPresent()) {
            List<Blob> images = productOpt.get().getImages();

            if (images != null && index >= 0 && index < images.size()) {
                Blob imageBlob = images.get(index);
                Resource imageResource = new InputStreamResource(imageBlob.getBinaryStream());
                
                MediaType mediaType = MediaTypeFactory
                        .getMediaType(imageResource)
                        .orElse(MediaType.IMAGE_JPEG);

                return ResponseEntity.ok().contentType(mediaType).body(imageResource);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/shop/{id}/comment")
    public String addComment(@PathVariable Long id, @RequestParam String content, HttpSession session) {
        try {
            Long userId = getCurrentUserId(session);
            commentService.addComment(userId, id, content);
            return "redirect:/shop/" + id;
        } catch (RuntimeException e) {
            return "redirect:/user/login";
        }
    }

    @PostMapping("/shop/{productId}/comment/edit/{commentId}")
    public String editComment(@PathVariable Long productId, @PathVariable Long commentId, @RequestParam String newContent, HttpSession session) {
        try {
            Long userId = getCurrentUserId(session);
            commentService.editComment(commentId, userId, newContent);
            return "redirect:/shop/" + productId;
        } catch (RuntimeException e) {
            return "redirect:/user/login";
        }
    }

    @PostMapping("/shop/{productId}/comment/delete/{commentId}")
    public String deleteComment(@PathVariable Long productId, @PathVariable Long commentId, HttpSession session) {
        try {
            Long userId = getCurrentUserId(session);
            commentService.deleteComment(commentId, userId);
            return "redirect:/shop/" + productId;
        } catch (RuntimeException e) {
            return "redirect:/user/login";
        }
    }

    @PostMapping("/add-to-cart")
    public String addToCart(@RequestParam Long productId,
                            @RequestParam(defaultValue = "1") int quantity,
                            RedirectAttributes redirectAttributes, 
                            HttpSession session) {
        try {
            Long userId = getCurrentUserId(session);
            cartService.addProductToCart(userId, productId, quantity);
            redirectAttributes.addFlashAttribute("successMessage", "Product added successfully to cart.");        
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Usuario no autenticado")) {
                return "redirect:/user/login";
            }
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        
        String referer = session.getAttribute("previousPage") != null ? session.getAttribute("previousPage").toString() : "/cart";
        return "redirect:" + referer;
    }
    

}
