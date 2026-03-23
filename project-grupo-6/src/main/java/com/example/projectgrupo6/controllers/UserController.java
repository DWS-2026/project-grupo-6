package com.example.projectgrupo6.controllers;

import com.example.projectgrupo6.domain.Comment;
import com.example.projectgrupo6.domain.Image;
import com.example.projectgrupo6.domain.Order;
import com.example.projectgrupo6.domain.User;
import com.example.projectgrupo6.services.CommentService;
import com.example.projectgrupo6.services.ImageService;
import com.example.projectgrupo6.services.OrderService;
import com.example.projectgrupo6.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import java.util.Map;
import java.util.Optional;

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
        
        // Método auxiliar para obtener el usuario conectado de forma limpia
        private User getSessionUser(HttpServletRequest request) {
            String email = request.getUserPrincipal().getName(); 
            return userService.findByEmail(email).orElse(null); 
        }   

        @GetMapping("/profile")
        public String profile(HttpServletRequest request, Model model) {

            User user = getSessionUser(request);            
            if(user == null) {
                return "redirect:/user/login";
            }

            model.addAttribute("user", user);
            model.addAttribute("isAdmin", request.isUserInRole("ADMIN"));

            return "profile";
        }

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

        @GetMapping("/comments")
        public String showUserComments(Model model, HttpServletRequest request) {
            User sessionUser = getSessionUser(request);            
            if (sessionUser == null) {
                return "redirect:/user/login";
            }

            model.addAttribute("isAdmin", request.isUserInRole("ADMIN"));

            List <Comment> userComments = commentService.findAllByUser(sessionUser.getId());
            model.addAttribute("comments", userComments);

            return "profile-comments";
        }

        @PostMapping("/comments/update/{commentId}")
        public String updateCommentFromProfile(@PathVariable Long commentId, 
                                            @RequestParam String newContent, 
                                            HttpServletRequest request) {
            
            User sessionUser = getSessionUser(request);
            if (sessionUser == null) return "redirect:/user/login";

            commentService.editComment(commentId, sessionUser.getId(), newContent);
            return "redirect:/user/comments";
        }

        @PostMapping("/comments/delete/{commentId}")
        public String deleteCommentFromProfile(@PathVariable Long commentId, 
                                            HttpServletRequest request) {
            
            User sessionUser = getSessionUser(request);
            if (sessionUser == null) return "redirect:/user/login";

            commentService.deleteComment(commentId, sessionUser.getId());
            return "redirect:/user/comments";
        }
        
        @GetMapping("/update")
        public String showUserProfile(HttpServletRequest request, Model model) {
            
            User sessionUser = getSessionUser(request);
            if (sessionUser == null) return "redirect:/user/login";

            model.addAttribute("user", sessionUser);
            model.addAttribute("isAdmin", request.isUserInRole("ADMIN"));

            return "profile-edit";
        }

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
            if (userToUpdate == null) return "redirect:/user/login";
            
            // Comprobamos si el usuario ha cambiado su email
            boolean emailChanged = !userToUpdate.getEmail().equals(email);

            userToUpdate.setFirstname(firstname);
            userToUpdate.setLastname(lastname);
            userToUpdate.setUsername(username);
            userToUpdate.setEmail(email);

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

            // IMPORTANTE: Si cambia el email, el login de Spring Security se vuelve inválido.
            // Le forzamos a hacer logout para que vuelva a iniciar sesión con el correo nuevo.
            if(emailChanged){
                return "redirect:/user/logout";
            }

            return "redirect:/user/profile";
        }

        @PostMapping ("/delete")
        public String deleteUser (Model model, User user, RedirectAttributes redirectAttributes, HttpServletRequest request){
            
            User sessionUser = getSessionUser(request);
            if (sessionUser == null) return "redirect:/user/login";

            userService.delete(sessionUser);
            redirectAttributes.addFlashAttribute("message", "User deleted successfully");
            
            // Redirigimos a la ruta mágica de Spring Security para destruir la sesión de verdad
            return "redirect:/user/logout";
        }

        @GetMapping("/orders")
        public String viewPurchaseHistory(HttpServletRequest request, Model model) {
            try {
                User sessionUser = getSessionUser(request);
                if (sessionUser == null) return "redirect:/user/login";

                model.addAttribute("isAdmin", request.isUserInRole("ADMIN"));

                List<Order> userOrders = orderService.findAllByUser(sessionUser);
                model.addAttribute("orders", userOrders);
                
                return "profile-orders"; 
                
            } catch (RuntimeException e) {
                return "redirect:/user/login";
            }
        }

}
