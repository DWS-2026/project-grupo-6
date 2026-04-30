package com.example.projectgrupo6.controllers.web;

import java.util.ArrayList;
import java.util.List;

import com.example.projectgrupo6.services.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.projectgrupo6.domain.Product;
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

    @Autowired
    private ValidationService validationService;

    @GetMapping("/add")
    public String renderProductForm(HttpSession session, Model model) {

        model.addAttribute("isAdmin", true); 
        model.addAttribute("isEdit", false);
        model.addAttribute("actionUrl", "/product/add");
        //For Mustache, it must have an object even if it's empty
        model.addAttribute("product", new Product());

        return "product-form";
    }

    //change
    @PostMapping("/add")
    public String addProduct(
            @RequestParam String name,
            @RequestParam String brand,
            @RequestParam Double price, 
            @RequestParam String category,
            @RequestParam String powerSource,
            @RequestParam(required = false) List<String> colors,
            @RequestParam String description,
            @RequestParam int stock,
            @RequestParam(required = false) String specification,
            @RequestParam(value = "images", required = false) MultipartFile[] imageFiles,
            RedirectAttributes attributes 
    ) {
        
        // --- SECURITY VALIDATION OF BACKEND ---
        List<String> sanitized = ValidationService.sanitizeAll(
                name, brand, category, powerSource, colors.toString(), description, specification
        );
        long validImagesCount = productService.checkNumberImages(imageFiles);
        
        if (validImagesCount > 4) {
            attributes.addFlashAttribute("errorMessage", "Security Error: Maximum 4 images allowed.");
            return "redirect:/product/add";
        }

        Product p = new Product();
        if(!validationService.isValidPrice(price)) price=0.0;

        productService.setAttbProduct(p, sanitized.get(0), sanitized.get(1), price, sanitized.get(2), sanitized.get(3), sanitized.get(5), sanitized.get(6));
        p.setColors(sanitized.get(4) != null ? colors : new ArrayList<>());

        if(!validationService.isValidStock(stock)) stock = 0;
        p.setStock(stock);

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

        } catch (Exception e) {
            e.printStackTrace();
            attributes.addFlashAttribute("errorMessage", "Error processing the files. Please try again.");
            return "redirect:/product/add";
        }

        productService.save(p); 
        return "redirect:/admin/products"; 
    }
}