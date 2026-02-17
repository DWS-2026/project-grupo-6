package com.example.projectgrupo6.controllers;

import com.example.projectgrupo6.domain.User;
import com.example.projectgrupo6.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    //@RequestMapping("/user"){

        @GetMapping ("/user/login")
        public String login(Model model) {
            model.addAttribute("user", new User());
            return "login";
        }

        @GetMapping ("/user/profile")
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



        @PostMapping ("/user/login")
        public String loginSubmit(@ModelAttribute("user") User user, Model model){
            /// ///////////////////////////////////////
            //Change redirection logic to error template
            /// ////////////////////////////////////////

            if(!user.getEmail().isEmpty() && !user.getPassword().isEmpty()){
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

        @PostMapping
        public String deleteUser (){

            return "redirect:index";
        }

    //}
}
