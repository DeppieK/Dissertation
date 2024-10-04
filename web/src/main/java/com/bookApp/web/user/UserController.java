package com.bookApp.web.user;


import com.bookApp.web.book.Book;
import com.bookApp.web.bookshelf.Bookshelf;
import com.bookApp.web.bookshelf.BookshelfService;
import com.bookApp.web.shelf_book.ShelfBookRepository;
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
import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService userService;
    private BookshelfService bookshelfService;
    private ShelfBookRepository shelfBookRepository;

    public UserController(UserService userService, BookshelfService bookshelfService, ShelfBookRepository shelfBookRepository) {
        this.userService = userService;
        this.bookshelfService = bookshelfService;
        this.shelfBookRepository = shelfBookRepository;
    }

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

        //capitalize the first letter of the first name and last name
        firstname = capitalizeFirstLetter(firstname);
        lastname = capitalizeFirstLetter(lastname);

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

    private String capitalizeFirstLetter(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }

    //profile page
    @GetMapping("/profile")
    public String profile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userService.findByUsername(username);
        long shelfId = bookshelfService.getShelfIdByUserAndLabel(user,"Read");
        long readCount = bookshelfService.countByShelfId(shelfId);
        Long booksInBookshelf = shelfBookRepository.countBooksByUser(user);

        model.addAttribute("user", user);
        model.addAttribute("readCount", readCount);
        model.addAttribute("booksInBookshelf", booksInBookshelf);


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