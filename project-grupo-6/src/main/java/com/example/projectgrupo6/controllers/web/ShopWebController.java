package com.example.projectgrupo6.controllers.web;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.example.projectgrupo6.services.*;

import com.example.projectgrupo6.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import jakarta.servlet.http.HttpServletRequest;
import com.example.projectgrupo6.domain.Product;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.core.io.Resource;
import java.sql.Blob;
import org.springframework.http.MediaType;


@Controller
public class ShopWebController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private ValidationService validationService;

    @GetMapping("/shop") 
    public String showshop(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "shop";
    }
    
    @GetMapping("/shop/{id}")
    public String showProduct(@PathVariable Long id, Model model, HttpServletRequest request) {
        
        Optional<Product> productOpt = productService.getById(id);

        if (productOpt.isPresent()) {
            model.addAttribute("product", productOpt.get());

            Long userId = userService.getCurrentUserId(request);

            List<Map<String, Object>> commentsView = commentService.getCommentsForProductView(id, userId);
            model.addAttribute("productComments", commentsView);

            return "shop-single";  
        } else {
            return "redirect:/shop";
        }
    }

    //changed ?? I think 
    @GetMapping("/product/{id}/image/{index}")
    public ResponseEntity<Resource> getProductImageByIndex(@PathVariable Long id, @PathVariable int index) throws SQLException {
        
        Optional<Product> productOpt = productService.getById(id);

        if (productOpt.isPresent()) {
            List<Blob> images = productOpt.get().getImages();

            if (images != null && index >= 0 && index < images.size()) {

                Resource imageResource = imageService.getImageResources(images, index);
                
                MediaType mediaType = MediaTypeFactory
                        .getMediaType(imageResource)
                        .orElse(MediaType.IMAGE_JPEG);

                return ResponseEntity.ok().contentType(mediaType).body(imageResource);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/shop/{id}/comment")
    public String addComment(@PathVariable Long id, @RequestParam String content, HttpServletRequest request) {
        String sanitizedContent = ValidationService.cleanAndSanitize(content);
        User user = userService.getSessionUser(request);
        commentService.addComment(user.getId(), id, sanitizedContent);
        return "redirect:/shop/" + id;
    }

    @PostMapping("/shop/{productId}/comment/edit/{commentId}")
    public String editComment(@PathVariable Long productId, 
                            @PathVariable Long commentId, 
                            @RequestParam String newContent, 
                            HttpServletRequest request) {

        String sanitizedContent = ValidationService.cleanAndSanitize(newContent);
        User user = userService.getSessionUser(request);
        commentService.editComment(commentId, user.getId(), sanitizedContent);
        return "redirect:/shop/" + productId;
    }

    @PostMapping("/shop/{productId}/comment/delete/{commentId}")
    public String deleteComment(@PathVariable Long productId, @PathVariable Long commentId, HttpServletRequest request) {
        User user = userService.getSessionUser(request);
        commentService.deleteComment(commentId, user.getId());
        return "redirect:/shop/" + productId;
    }

    //changed ?? I think
    @PostMapping("/add-to-cart")
    public String addToCart(@RequestParam Long productId,
                            @RequestParam(defaultValue = "1") int quantity,
                            RedirectAttributes redirectAttributes, 
                            HttpServletRequest request) {
        
        User user = userService.getSessionUser(request);
        try {
            if(!validationService.isValidQuantity(quantity)) quantity = 0;

            cartService.addProductToCart(user.getId(), productId, quantity);
            redirectAttributes.addFlashAttribute("successMessage", "Product added successfully to cart.");        
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        
        // Magic: request.getHeader("Referer") says in which URL was the user (ej. /shop/2)
        // Redirect to Cart by default
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/cart");
    }
    

}
