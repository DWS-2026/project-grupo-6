package com.example.projectgrupo6.controllers;

import com.example.projectgrupo6.domain.Comment;
import com.example.projectgrupo6.domain.Image;
import com.example.projectgrupo6.domain.Order;
import com.example.projectgrupo6.domain.Product;
import com.example.projectgrupo6.domain.User;
import com.example.projectgrupo6.services.CartService;
import com.example.projectgrupo6.services.CommentService;
import com.example.projectgrupo6.services.ImageService;
import com.example.projectgrupo6.services.OrderService;
import com.example.projectgrupo6.services.ProductService;
import com.example.projectgrupo6.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;
    @Autowired
    private ImageService imageService;
    @Autowired
    private ProductService productService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private OrderService orderService;

    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("isAdmin", true);
        
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin-user-list";
    }

    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable Long id, Model model) {
        
        model.addAttribute("isAdmin", true);

        Optional<User> userOptional = userService.getById(id);
        if (userOptional.isPresent()) {
            model.addAttribute("user", userOptional.get());
            return "admin-user-edit";
        } else {
            return "redirect:/admin/users";
        }
    }

    @PostMapping("/users/edit/{id}")
    public String editUserPost(
        @PathVariable Long id, 
        @RequestParam String firstname,
        @RequestParam String lastname,
        @RequestParam String email,
        @RequestParam String username,
        @RequestParam String rol,
        @RequestParam(value = "image", required = false) MultipartFile image, 
        RedirectAttributes redirectAttributes) {
        
        Optional<User> userOptional = userService.getById(id);
        
        if (userOptional.isPresent()) {
            User userToUpdate = userOptional.get();
            
            userToUpdate.setFirstname(firstname);
            userToUpdate.setLastname(lastname);
            userToUpdate.setEmail(email);
            userToUpdate.setUsername(username);
            userToUpdate.setRol(rol);

            if (image != null && !image.isEmpty()) {
                try {
                    Image saved = imageService.createImage(image);
                    userToUpdate.setProfileImage(new SerialBlob(saved.getImageFile()));
                } catch (Exception e) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Error updating the profile image.");
                    return "redirect:/admin/users/edit/" + id;
                }
            }

            userService.save(userToUpdate);
            redirectAttributes.addFlashAttribute("successMessage", "User profile updated successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "User cannot be updated.");
        }
        return "redirect:/admin/users/edit/" + id;
    }

    @GetMapping("/users/view/{id}")
    public String viewUser(@PathVariable Long id, Model model) {

        model.addAttribute("isAdmin", true);

        Optional<User> userOptional = userService.getById(id);
        if (userOptional.isPresent()) {
            model.addAttribute("user", userOptional.get());
            return "admin-user-view";
        } else {
            return "redirect:/admin/users";
        }
    }



    //PRODUCTS:

    @GetMapping("/products")
    public String listProducts(Model model) {
        
        model.addAttribute("isAdmin", true);

        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "admin-product-list";
    }

    @GetMapping("/products/{id}")
    public String showProductDetail(@PathVariable long id, Model model) {

        Optional<Product> op = productService.getById(id);
        if (op.isEmpty()) {
            return "redirect:/admin/products";
        }

        Product product = op.get();

        //Optional
        if (product.getSpecification() == null || product.getSpecification().isBlank()) {
            product.setSpecification(null); // Or null
        }
        model.addAttribute("product", product);
        return "product-details";
    }

    @GetMapping("/products/{productId}/edit")
    public String editProduct (@PathVariable long productId, Model model){

        model.addAttribute("isAdmin", true);

        Optional<Product> op = productService.getById(productId);
        if (op.isPresent()) {
            Product p = op.get();
            model.addAttribute("product", p);
            model.addAttribute("isEdit", true);
            model.addAttribute("actionUrl", "/admin/products/" + p.getId() + "/edit");
        } else {
            model.addAttribute("isEdit", false);
            model.addAttribute("actionUrl", "/product/add"); 
        }

        return "product-form";
    }

    @PostMapping("/products/{productId}/edit")
    public String editProductSubmit (@PathVariable long productId,
                                     @RequestParam String name,
                                     @RequestParam String brand,
                                     @RequestParam Double price,
                                     @RequestParam String category,
                                     @RequestParam String description,
                                     @RequestParam String specification,
                                     @RequestParam String powerSource,
                                     @RequestParam (value = "colors", required = false) List<String> colors,
                                     @RequestParam int stock,
                                     @RequestParam(value = "images", required = false) MultipartFile[] images,
                                     RedirectAttributes attributes) throws IOException{

        Optional<Product> op = productService.getById(productId);
        if(op.isPresent()){
            Product p = op.get();

            productService.setAttbProduct(p, name, brand, price, category, powerSource, description, specification);
            p.setColors(colors != null ? colors : new ArrayList<>());
            //p.setReviewCount(0);
            p.setStock(stock);


            if(images != null && Arrays.stream(images).anyMatch(f -> !f.isEmpty())) {
                boolean imageError = productService.setProductImages(p, images);
                if(imageError){
                    attributes.addFlashAttribute("errorMessage", "Error processing the files. Please try again.");
                    return "redirect:/admin/products/" + productId + "/edit";
                }
            } else {
                //We don't do anything, maintains its images
                if (p.getImages() == null || p.getImages().isEmpty()){
                    // Default image only if there are no new ones and there was no prior image
                    Blob defaultImage = imageService.loadImage("default-product.png");
                    p.setImages(List.of(defaultImage));
                }
            }

            productService.save(p);
            return "redirect:/admin/products";

        } else {
           return "redirect:/admin/products";
        }
    }

    @PostMapping("/products/{productId}/delete")
    public String deleteProd (@PathVariable long productId){
        productService.delete(productId);
        return "redirect:/admin/products";
    }

    @GetMapping("/users/{userId}/comments")
    public String viewUserCommentsAsAdmin(@PathVariable Long userId, Model model) {
        
        model.addAttribute("isAdmin", true);
        
        Optional<User> targetUserOpt = userService.findById(userId); 
        
        if (targetUserOpt.isPresent()) {
            User targetUser = targetUserOpt.get();
            model.addAttribute("targetUser", targetUser); 
            
            List<Comment> userComments = commentService.findAllByUser(targetUser.getId());
            model.addAttribute("comments", userComments);
            
            return "admin-comment-list";
        } else {
            return "redirect:/admin/users"; 
        }
    }

    @PostMapping("/users/{userId}/comments/{commentId}/edit")
    public String updateCommentAdmin(@PathVariable Long userId, 
                                     @PathVariable Long commentId, 
                                     @RequestParam String newContent, 
                                     HttpServletRequest request,
                                     RedirectAttributes redirectAttributes) {
        
        try {
            // Get the ID of the admin doing the edition
            String adminEmail = request.getUserPrincipal().getName();
            User adminUser = userService.findByEmail(adminEmail).orElseThrow();
            
            commentService.editComment(commentId, adminUser.getId(), newContent);
            redirectAttributes.addFlashAttribute("successMessage", "Review updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating the review.");
        }
        
        return "redirect:/admin/users/" + userId + "/comments";
    }

    @PostMapping("/users/{userId}/comments/{commentId}/delete")
    public String deleteCommentAdmin(@PathVariable Long userId, 
                                     @PathVariable Long commentId,
                                     HttpServletRequest request,
                                     RedirectAttributes redirectAttributes) {
        
        try {
            String adminEmail = request.getUserPrincipal().getName();
            User adminUser = userService.findByEmail(adminEmail).orElseThrow();

            commentService.deleteComment(commentId, adminUser.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Review deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting the review.");
        }
        
        return "redirect:/admin/users/" + userId + "/comments";
    }
    
    @GetMapping("/users/{userId}/orders")
    public String viewUserOrdersAsAdmin(@PathVariable Long userId, Model model) {
        
        model.addAttribute("isAdmin", true);
        
        Optional<User> targetUserOpt = userService.getById(userId); 
        
        if (targetUserOpt.isPresent()) {
            User targetUser = targetUserOpt.get();
            model.addAttribute("targetUser", targetUser); 
            
            List<Order> userOrders = orderService.findAllByUser(targetUser);
            model.addAttribute("orders", userOrders);
            return "admin-user-orders";

        } else {
            return "redirect:/admin/users"; 
        }
    }

    @PostMapping("/users/{userId}/orders/{orderId}/status")
    public String updateOrderStatusAsAdmin(@PathVariable Long userId, 
                                           @PathVariable Long orderId, 
                                           @RequestParam String newStatus, 
                                           RedirectAttributes redirectAttributes) {
        
        try {
            Optional<Order> orderOpt = orderService.findById(orderId);
            if(orderOpt.isPresent()) {
                Order order = orderOpt.get();
                order.setStatus(newStatus);
                orderService.save(order);
                redirectAttributes.addFlashAttribute("successMessage", "Order #" + orderId + " status updated to " + newStatus);
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating the order status.");
        }
        return "redirect:/admin/users/" + userId + "/orders";
    }

    @PostMapping("/users/{userId}/orders/{orderId}/delete")
    public String deleteOrderAsAdmin(@PathVariable Long userId, 
                                     @PathVariable Long orderId, 
                                     RedirectAttributes redirectAttributes) {
        
        try {
            orderService.deleteById(orderId);
            redirectAttributes.addFlashAttribute("successMessage", "Order deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting the order.");
        }
        return "redirect:/admin/users/" + userId + "/orders";
    }

    @PostMapping ("/users/delete/{id}")
    public String deleteUser (@PathVariable Long id, RedirectAttributes redirectAttributes){
        Optional<User> optionalUser = userService.getById(id);
        if(optionalUser.isPresent()){
            userService.delete(optionalUser.get());
            redirectAttributes.addFlashAttribute("success", "User deleted successfully");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "User not found.");
        }
        
        return "redirect:/admin/users";
    }

}
