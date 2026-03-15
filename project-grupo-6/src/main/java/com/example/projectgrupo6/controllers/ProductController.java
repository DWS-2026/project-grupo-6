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
    public String renderProductForm(HttpSession session) {
        User sessionUser = (User) session.getAttribute("user");

        if(sessionUser == null){
            return "redirect:/user/login";
        }

        if(userService.checkIfAdmin(sessionUser) == false){
            return "redirect:/";
        }
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
            RedirectAttributes attributes // <-- MAGIA para enviar mensajes de error a la vista
    ) {
        
        // --- VALIDACIÓN DE SEGURIDAD BACKEND ---
        // Contamos cuántos archivos reales nos han enviado (que no estén vacíos)
        long validImagesCount = 0;
        if (imageFiles != null) {
            for (MultipartFile file : imageFiles) {
                if (!file.isEmpty()) validImagesCount++;
            }
        }
        
        // Si nos intentan colar más de 4, bloqueamos y devolvemos error
        if (validImagesCount > 4) {
            attributes.addFlashAttribute("errorMessage", "Security Error: Maximum 4 images allowed.");
            return "redirect:/product/add";
        }

        Product p = new Product();
        p.setName(name);
        p.setBrand(brand);
        p.setPrice(price);
        p.setCategory(category);
        p.setPowerSource(powerSource);
        p.setDescription(description);
        p.setSpecification(specification);
        p.setColors(colors != null ? colors : new ArrayList<>());
        p.setReviewCount(0);

        List<java.sql.Blob> productImages = new ArrayList<>();
        
        try {
            // 1. Lógica de Imágenes
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

            // 2. Lógica del PDF (¡Descomentada y lista!)
            //if (documentation != null && !documentation.isEmpty()) {
            //    byte[] docBytes = documentation.getBytes();
            //    java.sql.Blob docBlob = new javax.sql.rowset.serial.SerialBlob(docBytes);
            //    p.setDocumentation(docBlob);
            //}

        } catch (Exception e) {
            e.printStackTrace();
            // Si algo falla al procesar, avisamos al admin
            attributes.addFlashAttribute("errorMessage", "Error processing the files. Please try again.");
            return "redirect:/product/add";
        }

        productService.save(p); 
        return "redirect:/admin/products"; 
    }

    @GetMapping("/{id}/image/{index}")
    public ResponseEntity<Object> getProductImage(@PathVariable Long id, @PathVariable int index) throws SQLException {
        
        // Buscamos el producto en la BD
        Optional<Product> productOpt = productService.getById(id); 
        
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            List<java.sql.Blob> images = product.getImages();
            
            // Comprobamos que tenga fotos y que el índice que pedimos exista
            if (images != null && index >= 0 && index < images.size()) {
                
                java.sql.Blob imageBlob = images.get(index);
                
                // Lo convertimos a Resource (igual que en vuestro ImageService)
                Resource imageFile = new InputStreamResource(imageBlob.getBinaryStream());
                
                // Adivinamos el tipo (JPEG, PNG...)
                MediaType mediaType = MediaTypeFactory
                        .getMediaType(imageFile)
                        .orElse(MediaType.IMAGE_JPEG);

                // Lo enviamos a la vista
                return ResponseEntity.ok()
                        .contentType(mediaType)
                        .body(imageFile);
            }
        }
        
        // Si falla algo, devolvemos 404
        return ResponseEntity.notFound().build();
    }
}