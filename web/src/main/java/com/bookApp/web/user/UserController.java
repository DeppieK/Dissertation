package com.bookApp.web.user;


import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    //login page
    @GetMapping("/login")
    public String login() {

        return "login";
    }

    //sign up page
    @GetMapping("/signup")
    public String signup() {
        return "signUp";
    }

    //register user
    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String confirmPassword,
                           @RequestParam String firstname,
                           @RequestParam String lastname,
                           @RequestParam String email,
                           RedirectAttributes redirectAttributes) {
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match");
            return "redirect:/signup";
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setEmail(email);
        user.setDateOfRegistration(LocalDateTime.now());

        userService.save(user);

        return "redirect:/login";
    }

    //profile page
    @GetMapping("/profile")
    public String profile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userService.findByUsername(username);
        model.addAttribute("user", user);
        return "profile";
    }

    @GetMapping("/myFriends")
    public String friendsList(Model model) {
        //Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //String username = authentication.getName();
        //User user = userService.findByUsername(username);

        return "friendsList";
    }

    @GetMapping("/discoverBooks")
    public String discoverBooks(Model model) {
        //Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //String username = authentication.getName();
        //User user = userService.findByUsername(username);

        return "discoverBooks";
    }
}