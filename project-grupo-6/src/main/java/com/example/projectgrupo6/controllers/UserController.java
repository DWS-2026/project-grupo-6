package com.example.projectgrupo6.controllers;

import com.example.projectgrupo6.domain.Image;
import com.example.projectgrupo6.domain.User;
import com.example.projectgrupo6.services.ImageService;
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
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

        //
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

        @GetMapping("/update")
        public String update (Model model, HttpSession session){
            User sessionUser = (User) session.getAttribute("user");

            //Check session
            if(!userService.validateSession(sessionUser, sessionUser.getId())){
                return "redirect:/";
                //Redirect to error
            }

            //model.addAttribute("formAction", "/user/update/" + id);
            model.addAttribute("edit", true);
            model.addAttribute("user", sessionUser);

            //Possible change: only show non-sensitive attributes
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

        @GetMapping("/list")
        public String userList (HttpSession session, Model model){
            //If admin: show list, otherwise redirect to profile
            User sessionUser = (User) session.getAttribute("user");
            if(!userService.checkIfAdmin(sessionUser)){
                return "redirect:/user/profile/{id}";
            }

            List<User> userList = userService.getAllUsers();
            if(userList.isEmpty()){
                return "redirect:/";
                //Add redirection to error or message of empty
            }

            model.addAttribute("users", userList);
            return "admin-user-page";
        }


        //POST
        @PostMapping("/login")
        public String processLogin(@RequestParam String email, 
                                @RequestParam String password, 
                                HttpSession session, 
                                Model model) {
            
            User userDb = userService.findByEmail(email);

            if (userDb != null && userService.logincheck(userDb, password)) {
                session.setAttribute("user", userDb);
                return "redirect:/"; 
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

        @PostMapping("/update")
        public String updateSubmit (@ModelAttribute User formUser, HttpSession session, RedirectAttributes redirectAttributes){
            User sessionUser = (User) session.getAttribute("user");

            //Check session
            if(!userService.validateSession(sessionUser, sessionUser.getId())){
                return "redirect:/";
                //Redirect to error
            }

            /// ////////////////
            //Clean input first then save

            //Update
            userService.updateDataUser(sessionUser, formUser);

            //Save session
            session.setAttribute("user", sessionUser);

            redirectAttributes.addFlashAttribute("success", "Updated data");
            return "profile";
        }

        @PostMapping ("/delete/{id}")
        public String deleteUser (@PathVariable Long id, Model model, User user, RedirectAttributes redirectAttributes){
            //Add logic
            userService.delete(user);
            redirectAttributes.addFlashAttribute("success", "User deleted successfully");
            return "redirect:/";
        }


}
