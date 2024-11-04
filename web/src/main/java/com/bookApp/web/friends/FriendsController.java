package com.bookApp.web.friends;

import com.bookApp.web.user.User;
import com.bookApp.web.user.UserService;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class FriendsController {

    private UserService userService;

    public FriendsController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/myFriends")
    public String friendsList(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);

        return "friendsList";
    }

}