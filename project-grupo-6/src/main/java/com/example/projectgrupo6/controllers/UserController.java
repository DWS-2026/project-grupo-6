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

        @GetMapping("/login")
        public String showlogin(Model model){
            return "login";
        }

        @GetMapping ("/logout")
        public String logout(HttpServletRequest request, RedirectAttributes redirectAttributes) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }

            //logout message
            redirectAttributes.addFlashAttribute("logoutMessage", "Has cerrado sesión correctamente.");

            return "redirect:/";
        }

        @GetMapping ("/new")
        public String register (Model model){
            model.addAttribute("user", new User());
            //model.addAttribute("formAction", "/user/new");
            model.addAttribute("edit", false);

            return "user-form";
        }

        @GetMapping("/profile")
        public String profile(HttpSession session, Model model) {

            User sessionUser = (User) session.getAttribute("user");
            if (sessionUser == null) {
                return "redirect:/user/login"; 
            }

            User user = userService.getById(sessionUser.getId()).orElse(sessionUser);
            model.addAttribute("user", user);
            if(userService.checkIfAdmin(sessionUser)){
                model.addAttribute("isAdmin", true);
            }

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
        
        @PostMapping("/login")
        public String processLogin(@RequestParam String email, 
                                @RequestParam String password, 
                                HttpSession session, 
                                Model model) {
            
            User userDb = userService.findByEmail(email);

            if (userDb != null && userService.logincheck(userDb, password)) {
                session.setAttribute("user", userDb);
                return "redirect:/user/profile"; 
            } else {
                model.addAttribute("error", "Email o contraseña incorrectos");
                return "login";
            }
        }

        @PostMapping ("/logout")
        public String logoutSubmit(HttpSession session, RedirectAttributes redirectAttributes){
            if (session != null) {
                session.invalidate();
            }

            redirectAttributes.addFlashAttribute("logoutMessage", "Has cerrado sesión correctamente.");

            /*  //With Spring Security (?)
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                new SecurityContextLogoutHandler().logout(request, response, auth);
            }*/

            return "redirect:/";
        }

        @PostMapping ("/new")
        public String registerSubmit (@ModelAttribute("user") User user, @RequestParam("confirmPassword") String confirmPassword, MultipartFile image, Model model, HttpSession session) throws Exception{

            //Search email & username doesn't already exist
            if (userService.findByEmail(user.getEmail()) != null
                    || userService.findByUsername(user.getUsername()) != null) {
                //error
                System.out.println("DEBUG: user = " + user);
                model.addAttribute("error", "User with those credentials already exists");
                model.addAttribute("user", user);
                return "user-form";
            }

            //Then check correct password twice:
            if(!userService.checkCreatePassword(user.getPassword(), confirmPassword)){
                System.out.println("DEBUG: user = " + user);
                model.addAttribute("error", "Passwords don't match");
                model.addAttribute("user", user);
                return "user-form";
            }

            // Set image
            if(!image.isEmpty()){
                try {
                    Image saved = imageService.createImage(image);
                    user.setProfileImage(new SerialBlob(saved.getImageFile()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            //Then save:
            userService.save(user);

            //Automathic login
            session.setAttribute("user", user);

            return "profile";
        }

        @GetMapping("/comments")
        public String showUserComments(Model model, HttpSession session) {
            User sessionUser = (User) session.getAttribute("user");
            if (sessionUser == null) {
                return "redirect:/user/login";
            }

            if(userService.checkIfAdmin(sessionUser) == true){
            model.addAttribute("isAdmin", true);
            }

            List <Comment> userComments = commentService.findAllByUser(sessionUser.getId());
            model.addAttribute("comments", userComments);

            return "profile-comments";
        }

        @PostMapping("/comments/update/{commentId}")
        public String updateCommentFromProfile(@PathVariable Long commentId, 
                                            @RequestParam String newContent, 
                                            HttpSession session) {
            
            User sessionUser = (User) session.getAttribute("user");
            if (sessionUser == null) {
                return "redirect:/user/login";
            }

            commentService.editComment(commentId, sessionUser.getId(), newContent);

            return "redirect:/user/comments"; 
        }

        @PostMapping("/comments/delete/{commentId}")
        public String deleteCommentFromProfile(@PathVariable Long commentId, 
                                            HttpSession session) {
            
            User sessionUser = (User) session.getAttribute("user");
            if (sessionUser == null) {
                return "redirect:/user/login";
            }

            commentService.deleteComment(commentId, sessionUser.getId());

            return "redirect:/user/comments";
        }
        
        @GetMapping("/update")
        public String showUserProfile(HttpSession session, Model model) {
            
            User sessionUser = (User) session.getAttribute("user");
            
            if (sessionUser == null) {
                return "redirect:/user/login";
            }

            Optional<User> freshUser = userService.getById(sessionUser.getId());
            
            if (freshUser.isPresent()) {
                User user = freshUser.get();
                
                model.addAttribute("user", user);
                
                session.setAttribute("user", user);
            } else {
                session.invalidate(); 
                return "redirect:/user/login";
            }

            model.addAttribute("isAdmin", userService.checkIfAdmin(sessionUser));

            return "profile-edit"; 
        }

        @PostMapping("/update")
        public String updateUserProfile(
            @RequestParam String firstname,
            @RequestParam String lastname,
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile, 
            HttpSession session,
            RedirectAttributes redirectAttributes) {

            User sessionUser = (User) session.getAttribute("user");
            
            if (sessionUser == null) {
                return "redirect:/user/login";
            }

            Optional<User> userOptional = userService.getById(sessionUser.getId());
            
            if (userOptional.isPresent()) {
                User userToUpdate = userOptional.get();
                
                userToUpdate.setFirstname(firstname);
                userToUpdate.setLastname(lastname);
                userToUpdate.setUsername(username);
                userToUpdate.setEmail(email);

                // --- NUEVA LÓGICA DE IMAGEN ---
                if (imageFile != null && !imageFile.isEmpty()) {
                    try {
                        Image saved = imageService.createImage(imageFile);
                        userToUpdate.setProfileImage(new SerialBlob(saved.getImageFile()));
                    } catch (Exception e) {
                        redirectAttributes.addFlashAttribute("errorMessage", "Error updating your profile image.");
                        return "redirect:/user/profile"; 
                    }
                }

                User savedUser = userService.save(userToUpdate);

                session.setAttribute("user", savedUser);

                redirectAttributes.addFlashAttribute("successMessage", "Your profile has been updated successfully!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while updating your profile.");
            }

            return "redirect:/user/profile";
        }

        @PostMapping ("/delete/{id}")
        public String deleteUser (@PathVariable Long id, Model model, User user, RedirectAttributes redirectAttributes){
            //Add logic
            userService.delete(user);
            redirectAttributes.addFlashAttribute("success", "User deleted successfully");
            return "redirect:/";
        }

        @GetMapping("/orders")
        public String viewPurchaseHistory(HttpSession session, Model model) {
            try {
                User sessionUser = (User) session.getAttribute("user");
                if (sessionUser == null) {
                    return "redirect:/user/login";
                }

                if(userService.checkIfAdmin(sessionUser) == true){
                model.addAttribute("isAdmin", true);
                }

                List<Order> userOrders = orderService.findAllByUser(sessionUser);
                
                model.addAttribute("orders", userOrders);
                
                return "profile-orders"; 
                
            } catch (RuntimeException e) {
                return "redirect:/user/login";
            }
        }

}
