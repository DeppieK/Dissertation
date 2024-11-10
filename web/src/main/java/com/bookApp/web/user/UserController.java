package com.bookApp.web.user;


import com.bookApp.web.book.Book;
import com.bookApp.web.bookshelf.Bookshelf;
import com.bookApp.web.bookshelf.BookshelfService;
import com.bookApp.web.friends.Friends;
import com.bookApp.web.friends.FriendsRepository;
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
    @Autowired
    private Book book;
    @Autowired
    private FriendsRepository friendsRepository;

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

        //make a function for this ;)
        Bookshelf bookshelf1 = new Bookshelf();
        bookshelf1.setLabel("Currently Reading");
        bookshelf1.setUser(user);
        bookshelf1.setShelfId(bookshelfService.getNextShelfId());
        bookshelfService.save(bookshelf1);

        Bookshelf bookshelf2 = new Bookshelf();
        bookshelf2.setLabel("Read");
        bookshelf2.setUser(user);
        bookshelf2.setShelfId(bookshelfService.getNextShelfId());
        bookshelfService.save(bookshelf2);

        Bookshelf bookshelf3 = new Bookshelf();
        bookshelf3.setLabel("Want to Read");
        bookshelf3.setUser(user);
        bookshelf3.setShelfId(bookshelfService.getNextShelfId());
        bookshelfService.save(bookshelf3);

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

        List<Friends> friends = friendsRepository.findAllBySenderOrReceiverAndStatus(user, Friends.Status.ACCEPTED);
        int friendsCount = friends.size();

        model.addAttribute("user", user);
        model.addAttribute("readCount", readCount);
        model.addAttribute("booksInBookshelf", booksInBookshelf);
        model.addAttribute("friendsCount", friendsCount);


        return "profile";
    }

    @GetMapping("/discoverBooks")
    public String discoverBooks(Model model) {
        //Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //String username = authentication.getName();
        //User user = userService.findByUsername(username);

        return "discoverBooks";
    }
}