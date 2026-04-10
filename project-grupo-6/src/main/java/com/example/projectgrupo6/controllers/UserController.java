package com.example.projectgrupo6.controllers;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.projectgrupo6.domain.Comment;
import com.example.projectgrupo6.domain.Image;
import com.example.projectgrupo6.domain.Order;
import com.example.projectgrupo6.domain.User;
import com.example.projectgrupo6.services.CommentService;
import com.example.projectgrupo6.services.ImageService;
import com.example.projectgrupo6.services.OrderService;
import com.example.projectgrupo6.services.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private OrderService orderService;

    //move to service
        // Helpful method to get user in a cleaner way
        private User getSessionUser(HttpServletRequest request) {
            String email = request.getUserPrincipal().getName(); 
            return userService.findByEmail(email).orElse(null); 
        }

        //change
        @GetMapping("/profile")
        public String profile(HttpServletRequest request, Model model) {

            User user = getSessionUser(request);            
            if(user == null) {
                return "redirect:/login";
            }

            model.addAttribute("user", user);
            model.addAttribute("isAdmin", request.isUserInRole("ADMIN"));

            return "profile";
        }

        //change
        @GetMapping("/{id}/image")
        public ResponseEntity<Object> getImageFile(@PathVariable long id) throws SQLException {
            Optional<User> op_user = userService.findById(id);
            if(op_user.isPresent() && op_user.get().getProfileImage() != null) {
                Blob image = op_user.get().getProfileImage();
                Resource imageFile = new InputStreamResource(image.getBinaryStream());

                MediaType mediaType = MediaTypeFactory
                        .getMediaType(imageFile)
                        .orElse(MediaType.IMAGE_JPEG);

                return ResponseEntity
                        .ok()
                        .contentType(mediaType)
                        .body(imageFile);
            } else {
                return ResponseEntity.badRequest().build();
            }
        }

        //cahnge
        @GetMapping("/comments")
        public String showUserComments(Model model, HttpServletRequest request) {
            User sessionUser = getSessionUser(request);            
            if (sessionUser == null) {
                return "redirect:/login";
            }

            model.addAttribute("isAdmin", request.isUserInRole("ADMIN"));

            List <Comment> userComments = commentService.findAllByUser(sessionUser.getId());
            model.addAttribute("comments", userComments);

            return "profile-comments";
        }

        //change
        @PostMapping("/comments/edit/{commentId}")
        public String updateCommentFromProfile(@PathVariable Long commentId, 
                                            @RequestParam String newContent, 
                                            HttpServletRequest request) {
            
            User sessionUser = getSessionUser(request);
            if (sessionUser == null) return "redirect:/login";

            commentService.editComment(commentId, sessionUser.getId(), newContent);
            return "redirect:/user/comments";
        }

        //change
        @PostMapping("/comments/delete/{commentId}")
        public String deleteCommentFromProfile(@PathVariable Long commentId, 
                                            HttpServletRequest request) {
            
            User sessionUser = getSessionUser(request);
            if (sessionUser == null) return "redirect:/login";

            commentService.deleteComment(commentId, sessionUser.getId());
            return "redirect:/user/comments";
        }

        //change
        @GetMapping("/update")
        public String showUserProfile(HttpServletRequest request, Model model) {
            
            User sessionUser = getSessionUser(request);
            if (sessionUser == null) return "redirect:/login";

            model.addAttribute("user", sessionUser);
            model.addAttribute("isAdmin", request.isUserInRole("ADMIN"));

            return "profile-edit";
        }

        //change
        @PostMapping("/update")
        public String updateUserProfile(
            @RequestParam String firstname,
            @RequestParam String lastname,
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile, 
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

            User userToUpdate = getSessionUser(request);
            if (userToUpdate == null) return "redirect:/login";
            
            String newEmail = email.trim();    

            // Check if user has changed its email
            boolean emailChanged = !userToUpdate.getEmail().equalsIgnoreCase(newEmail);

            userToUpdate.setFirstname(firstname);
            userToUpdate.setLastname(lastname);
            userToUpdate.setUsername(username);
            userToUpdate.setEmail(newEmail);

            if (imageFile != null && !imageFile.isEmpty()) {
                try {
                    Image saved = imageService.createImage(imageFile);
                    userToUpdate.setProfileImage(new SerialBlob(saved.getImageFile()));
                } catch (Exception e) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Error updating your profile image.");
                    return "redirect:/user/profile"; 
                }
            }

            userService.save(userToUpdate);
            redirectAttributes.addFlashAttribute("successMessage", "Your profile has been updated successfully!");

            // IMPORTANT: If email changes, Spring's login becomes invalid
            // We force to log out and login again with the new email
            if (emailChanged) {
                System.out.println("DEBUG PROFILE UPDATE:");
            System.out.println("Email en DB: [" + userToUpdate.getEmail() + "]");
            System.out.println("Email del Form: [" + email + "]");
            System.out.println("¿Detecta cambio?: " + emailChanged);
                try {
                    request.logout();
                } catch (ServletException e) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Error logging out after email change. Please log in again.");
                }

                SecurityContextHolder.clearContext();

                redirectAttributes.addFlashAttribute("successMessage", "Email actualizado. Por favor, inicia sesión de nuevo.");
                return "redirect:/login";
            }
            return "redirect:/user/profile";
        }

        //change
        @PostMapping ("/delete")
        public String deleteUser (Model model, User user, RedirectAttributes redirectAttributes, HttpServletRequest request){
            
            User sessionUser = getSessionUser(request);
            if (sessionUser == null) return "redirect:/login";

            userService.delete(sessionUser);
            redirectAttributes.addFlashAttribute("message", "User deleted successfully");
            
            // Redirect to the magic route of Spring Security to destroy the session
            return "redirect:/user/logout";
        }

        //change
        @GetMapping("/orders")
        public String viewPurchaseHistory(HttpServletRequest request, Model model) {
            try {
                User sessionUser = getSessionUser(request);
                if (sessionUser == null) return "redirect:/login";

                model.addAttribute("isAdmin", request.isUserInRole("ADMIN"));

                List<Order> userOrders = orderService.findAllByUser(sessionUser);
                model.addAttribute("orders", userOrders);
                
                return "profile-orders"; 
                
            } catch (RuntimeException e) {
                return "redirect:/login";
            }
        }

}
