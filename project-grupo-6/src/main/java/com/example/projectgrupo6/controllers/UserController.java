package com.example.projectgrupo6.controllers;

import com.example.projectgrupo6.domain.User;
import com.example.projectgrupo6.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

        @GetMapping("/login")
        public String login(Model model) {
            // Change "user" to "loginForm" to avoid conflicts
            
            return "login";
        }

        @GetMapping ("/logout")
        public String logout(Model model) {
            model.addAttribute("user", new User());
            //add functionality


            return "login";
        }

        @GetMapping ("/new")
        public String register (Model model){
            model.addAttribute("user", new User());
            return "user-form";
        }

        @GetMapping("/update")
        public String update (Model model, HttpSession session){
            User user = (User) session.getAttribute("user");
            model.addAttribute("user", user);
            return "user-form";
        }

        @GetMapping ("/profile")
        public String profile(HttpSession session, Model model){
            User user = (User) session.getAttribute("user");

            //Change to redirect to error
            if (user == null) {
                return "redirect:/user/login";
            }

            model.addAttribute("user", user);

            // Initials for fallback image
            String initials =
                    user.getFirstname().substring(0, 1).toUpperCase() +
                            user.getLastname().substring(0, 1).toUpperCase();

            model.addAttribute("initials", initials);
            return "profile";
        }

        @GetMapping("/list")
        public String userList (HttpSession session, Model model){
            List<User> userList = userService.getAllUsers();
            if(userList.isEmpty()){
                //Add redirection to error or message of empty
            }

            model.addAttribute("users", userList);
            return "admin-user-page";
        }



        // POST
        @PostMapping ("/login")
        public String loginSubmit(@RequestParam String email, 
                           @RequestParam String password, 
                           HttpSession session, Model model) {
            /// ///////////////////////////////////////
            //Change redirection logic to error template
            /// ////////////////////////////////////////

            User userDb = userService.findByEmail(email);

            if (userDb != null && userService.logincheck(userDb, password)) {
                // Guardamos al usuario directamente en la sesión de Jakarta
                session.setAttribute("loggedUser", userDb);
                return "redirect:/";
            }
            model.addAttribute("error", "Invalid email or password");
            return "login";
        }

        @PostMapping ("/logout")
        public String logoutSubmit(@ModelAttribute("user") User user, HttpSession httpSession, Model model){
            /// ///////////////////////////////////////
            //Add logout logic
            /// ////////////////////////////////////////
            httpSession.invalidate();

            return "redirect:/index";
        }

        @PostMapping ("/new")
        public String registerSubmit (@ModelAttribute("user") User user, @RequestParam("confirmPassword") String confirmPassword, MultipartFile image, Model model) throws Exception{
            //Search email & username doesn´t already exist
            if (userService.findByEmail(user.getEmail()) != null
                    || userService.findByUsername(user.getUsername()) != null) {

                //Then check correct password twice:
                if(!userService.checkCreatePassword(user.getPassword(), confirmPassword)){
                    model.addAttribute("error", "Passwords don't match");
                    return "user-form";
                }

                //Then save:
                userService.save(user, image);

                return "redirect:profile";
            }

            //error
            model.addAttribute("error", "User with those credentials already exists");
            return "user-form";
        }

        @PostMapping("/update")
        public String updateSubmit (@ModelAttribute User formUser, HttpSession session){
            User sessionUser = (User) session.getAttribute("user");

            /// ////////////////
            //Clean input first then save
            /// ///////////////

            userService.updateDataUser(sessionUser, formUser);

            return "user-form";
        }

        @PostMapping ("/delete")
        public String deleteUser (Model model, User user){
            //Add logic
            userService.delete(user);
            return "redirect:index";
        }


}
