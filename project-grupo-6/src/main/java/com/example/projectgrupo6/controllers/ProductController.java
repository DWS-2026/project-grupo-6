package com.example.projectgrupo6.controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;


import com.example.projectgrupo6.domain.Product;
import com.example.projectgrupo6.domain.User;
import com.example.projectgrupo6.services.ImageService;
import com.example.projectgrupo6.services.ProductService;
import com.example.projectgrupo6.services.UserService;

import jakarta.servlet.http.HttpSession; 

@Controller 
@RequestMapping("/product")
public class ProductController {

    @Autowired 
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

    @GetMapping("/add")
    public String renderProductForm(HttpSession session, Model model) {
        User sessionUser = (User) session.getAttribute("user");

        if(sessionUser == null){
            return "redirect:/user/login";
        }

        if(userService.checkIfAdmin(sessionUser) == false){
            model.addAttribute("isAdmin", false);
            return "redirect:/";
        }
        model.addAttribute("isAdmin", true);
        model.addAttribute("isEdit", false);

        return "product-form";
    }

    @PostMapping("/add")
    public String addProduct(
            @RequestParam String name,
            @RequestParam String brand,
            @RequestParam Double price, 
            @RequestParam String category,
            @RequestParam String powerSource,
            @RequestParam(required = false) List<String> colors,
            @RequestParam String description,
            @RequestParam(required = false) String specification,
            @RequestParam(value = "imageFiles", required = false) MultipartFile[] imageFiles,
            @RequestParam(value = "documentation", required = false) MultipartFile documentation,
            RedirectAttributes attributes // <-- MAGIC to send messages of error to the view
    ) {
        
        // --- SECURITY VALIDATION OF BACKEND ---
        // Count how many real archives are sent (not empty)
        long validImagesCount = productService.checkNumberImages(imageFiles);
        
        // If more than 4, block
        if (validImagesCount > 4) {
            attributes.addFlashAttribute("errorMessage", "Security Error: Maximum 4 images allowed.");
            return "redirect:/product/add";
        }

        Product p = new Product();
        productService.setAttbProduct(p, name, brand, price, category, powerSource, description, specification);
        p.setColors(colors != null ? colors : new ArrayList<>());
        p.setReviewCount(0);

        List<java.sql.Blob> productImages = new ArrayList<>();
        
        try {
            // 1. Images logic
            if (imageFiles != null && imageFiles.length > 0) {
                for (MultipartFile file : imageFiles) {
                    if (!file.isEmpty()) {
                        byte[] bytes = file.getBytes();
                        java.sql.Blob blob = new javax.sql.rowset.serial.SerialBlob(bytes);
                        productImages.add(blob);
                    }
                }
            }
            
            if (productImages.isEmpty()) {
                productImages.add(imageService.loadImage("default-product.png"));
            }
            p.setImages(productImages);

            // 2. Logic of PDF
            if (documentation != null && !documentation.isEmpty()) {
                byte[] docBytes = documentation.getBytes();
                java.sql.Blob docBlob = new javax.sql.rowset.serial.SerialBlob(docBytes);
                p.setDocumentation(docBlob);
            }

        } catch (Exception e) {
            e.printStackTrace();
            // If something fails to process, warn admin
            attributes.addFlashAttribute("errorMessage", "Error processing the files. Please try again.");
            return "redirect:/product/add";
        }

        productService.save(p); 
        return "redirect:/admin/products"; 
    }

    @GetMapping("/{id}/image/{index}")
    public ResponseEntity<Object> getProductImage(@PathVariable Long id, @PathVariable int index) throws SQLException {
        
        // Search product
        Optional<Product> productOpt = productService.getById(id); 
        
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            List<java.sql.Blob> images = product.getImages();
            
            // Check if it has images
            if (images != null && index >= 0 && index < images.size()) {
                
                java.sql.Blob imageBlob = images.get(index);
                
                // Change to Resource
                Resource imageFile = new InputStreamResource(imageBlob.getBinaryStream());
                
                // Guess the type (JPEG, PNG...)
                MediaType mediaType = MediaTypeFactory
                        .getMediaType(imageFile)
                        .orElse(MediaType.IMAGE_JPEG);

                // Send to view
                return ResponseEntity.ok()
                        .contentType(mediaType)
                        .body(imageFile);
            }
        }
        
        // If it fails -> 404
        return ResponseEntity.notFound().build();
    }
}