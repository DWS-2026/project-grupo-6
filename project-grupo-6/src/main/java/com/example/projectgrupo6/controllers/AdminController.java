package com.example.projectgrupo6.controllers;

import com.example.projectgrupo6.domain.Image;
import com.example.projectgrupo6.domain.Product;
import com.example.projectgrupo6.domain.User;
import com.example.projectgrupo6.services.CartService;
import com.example.projectgrupo6.services.CommentService;
import com.example.projectgrupo6.services.ImageService;
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

            userService.save(userToUpdate);
            
            
            redirectAttributes.addFlashAttribute("successMessage", "User profile updated successfully!");

            return "redirect:/admin/users/edit/{id}";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot be updated.");
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



}
