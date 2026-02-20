package com.example.projectgrupo6.controllers;

import com.example.projectgrupo6.domain.User;
import com.example.projectgrupo6.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private User sessionUser;

        @GetMapping("/login")
        public String login(Model model) {
            // Cambiamos "user" por "loginForm" para evitar conflictos
            //model.addAttribute("loginForm", new User());
            return "login";
        }

        @GetMapping ("/new")
        public String register (Model model){
            model.addAttribute("user", new User());
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



        // POST
        @PostMapping ("/login")
        public String loginSubmit(@ModelAttribute("user") User user, Model model){
            /// ///////////////////////////////////////
            //Change redirection logic to error template
            /// ////////////////////////////////////////

            if(userService.correctLoginInput(user.getEmail(), user.getPassword())){
                User findUser = userService.findByEmail(user.getEmail());
                if(findUser == null){
                    model.addAttribute("error", "You're not registered");
                    return "login";
                }

                if (!userService.logincheck(findUser, findUser.getPassword())){
                    model.addAttribute("error", "Invalid credentials");
                    return "login";
                }
            }
            return "redirect:/user/profile";
        }

        @PostMapping ("/new")
        public String registerSubmit (@ModelAttribute("user") User user, @RequestParam("confirmPassword") String confirmPassword, Model model){
            //Search email & username doesn´t already exist
            if (userService.findByEmail(user.getEmail()) != null
                    || userService.findByUsername(user.getUsername()) != null) {

                //Then check correct password twice:
                if(!userService.checkCreatePassword(user.getPassword(), confirmPassword)){
                    model.addAttribute("error", "Passwords don't match");
                    return "user-form";
                }

                //Then save:
                userService.save(user);

                return "redirect:profile";
            }

            //error
            model.addAttribute("error", "User with those credentials already exists");
            return "user-form";
        }

        @PostMapping ("/delete")
        public String deleteUser (Model model){
            //Add logic

            return "redirect:index";
        }


}
