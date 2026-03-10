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
    private CartService cartService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private OrderService orderService;

    @GetMapping("/users")
    public String listUsers(HttpSession session, Model model) {
        User sessionUser = (User) session.getAttribute("user");

        if(sessionUser == null){
            return "redirect:/user/login";
        }

        if(userService.checkIfAdmin(sessionUser) == false){
            return "redirect:/";
        }

        model.addAttribute("isAdmin", true);

        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin-user-list";
    }

    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable Long id, HttpSession session, Model model) {
        
        User sessionUser = (User) session.getAttribute("user");

        if(sessionUser == null){
            return "redirect:/user/login";
        }

        if(userService.checkIfAdmin(sessionUser) == false){
            return "redirect:/";
        }

        model.addAttribute("isAdmin", true);

        Optional<User> userOptional = userService.getById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            model.addAttribute("user", user);
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
        HttpSession session,
        RedirectAttributes redirectAttributes) {
        
        User sessionUser = (User) session.getAttribute("user");

        if(sessionUser == null){
            return "redirect:/user/login";
        }

        if(userService.checkIfAdmin(sessionUser) == false){
            return "redirect:/";
        }

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
            return "redirect:/admin/users/edit/{id}";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "User cannot be updated.");
            return "redirect:/admin/users/edit/{id}";
        }
    }

    @GetMapping("/users/view/{id}")
    public String viewUser(@PathVariable Long id, HttpSession session, Model model) {
        
        User sessionUser = (User) session.getAttribute("user");

        if(sessionUser == null){
            return "redirect:/user/login";
        }

        if(userService.checkIfAdmin(sessionUser) == false){
            return "redirect:/";
        }

        model.addAttribute("isAdmin", true);

        Optional<User> userOptional = userService.getById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            model.addAttribute("user", user);
            return "admin-user-view";
        } else {
            return "redirect:/admin/users";
        }
    }

    @GetMapping("/products")
    public String listProducts(HttpSession session, Model model) {
        
        User sessionUser = (User) session.getAttribute("user");

        if(sessionUser == null){
            return "redirect:/user/login";
        }

        if(userService.checkIfAdmin(sessionUser) == false){
            return "redirect:/";
        }

        model.addAttribute("isAdmin", true);

        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "admin-product-list";
    }

    @GetMapping("/users/{userId}/comments")
    public String viewUserCommentsAsAdmin(@PathVariable Long userId, Model model, HttpSession session) {
        
        User sessionUser = (User) session.getAttribute("user");
        
        if (sessionUser == null || !userService.checkIfAdmin(sessionUser)) {
            return "redirect:/user/login"; 
        }

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
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        
        User sessionUser = (User) session.getAttribute("user");
        
        if (sessionUser != null && userService.checkIfAdmin(sessionUser)) {
            try {
                commentService.editComment(commentId, sessionUser.getId(), newContent);
                redirectAttributes.addFlashAttribute("successMessage", "Review updated successfully!");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Error updating the review.");
            }
        }
        
        return "redirect:/admin/users/" + userId + "/comments";
    }

    @PostMapping("/users/{userId}/comments/{commentId}/delete")
    public String deleteCommentAdmin(@PathVariable Long userId, 
                                     @PathVariable Long commentId, 
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        
        User sessionUser = (User) session.getAttribute("user");
        
        if (sessionUser != null && userService.checkIfAdmin(sessionUser)) {
            try {
                commentService.deleteComment(commentId, sessionUser.getId());
                redirectAttributes.addFlashAttribute("successMessage", "Review deleted successfully!");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Error deleting the review.");
            }
        }
        
        return "redirect:/admin/users/" + userId + "/comments";
    }
    
    @GetMapping("/users/{userId}/orders")
    public String viewUserOrdersAsAdmin(@PathVariable Long userId, Model model, HttpSession session) {
        
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null || !userService.checkIfAdmin(sessionUser)) {
            return "redirect:/user/login"; 
        }

        model.addAttribute("isAdmin", true);
        
        Optional<User> targetUserOpt = userService.getById(userId); 
        
        if (targetUserOpt.isPresent()) {
            User targetUser = targetUserOpt.get();
            model.addAttribute("targetUser", targetUser); 
            
            List<Order> userOrders = orderService.findAllByUser(targetUser);
            model.addAttribute("orders", userOrders);
            
            return "admin-user-orders"; // El nombre de nuestra nueva plantilla
        } else {
            return "redirect:/admin/users"; 
        }
    }

    @PostMapping("/users/{userId}/orders/{orderId}/status")
    public String updateOrderStatusAsAdmin(@PathVariable Long userId, 
                                           @PathVariable Long orderId, 
                                           @RequestParam String newStatus, 
                                           HttpSession session,
                                           RedirectAttributes redirectAttributes) {
        
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser != null && userService.checkIfAdmin(sessionUser)) {
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
        }
        return "redirect:/admin/users/" + userId + "/orders";
    }

    @PostMapping("/users/{userId}/orders/{orderId}/delete")
    public String deleteOrderAsAdmin(@PathVariable Long userId, 
                                     @PathVariable Long orderId, 
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser != null && userService.checkIfAdmin(sessionUser)) {
            try {
                orderService.deleteById(orderId);
                redirectAttributes.addFlashAttribute("successMessage", "Order deleted successfully.");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Error deleting the order.");
            }
        }
        return "redirect:/admin/users/" + userId + "/orders";
    }

    @PostMapping ("/users/delete/{id}")
    public String deleteUser (@PathVariable Long id, Model model, User user, RedirectAttributes redirectAttributes){
        //Add logic
        userService.delete(user);
        redirectAttributes.addFlashAttribute("success", "User deleted successfully");
        return "redirect:/admin/users";
    }

}
