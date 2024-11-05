package com.bookApp.web.friends;

import com.bookApp.web.user.User;
import com.bookApp.web.user.UserService;
import org.springframework.ui.Model;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


@Controller
public class FriendsController {

    private final FriendsRepository friendsRepository;
    private UserService userService;

    public FriendsController(UserService userService, FriendsRepository friendsRepository) {
        this.userService = userService;
        this.friendsRepository = friendsRepository;
    }

    @GetMapping("/myFriends")
    public String friendsList(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);

        List<Friends> friends = friendsRepository.findAllBySenderOrReceiverAndStatus(user, Friends.Status.ACCEPTED);
        List<Friends> friendRequests = friendsRepository.findAllByReceiverAndStatus(user,Friends.Status.PENDING);
        List<Friends> declinedRequests = friendsRepository.findAllByReceiverAndStatus(user,Friends.Status.DECLINED);
        List<Friends> sentRequests = friendsRepository.findAllBySenderAndStatus(user, Friends.Status.PENDING);

        model.addAttribute("currentUser", user);
        model.addAttribute("friends", friends);
        model.addAttribute("friendRequests", friendRequests);
        model.addAttribute("declinedRequests", declinedRequests);
        model.addAttribute("sentRequests", sentRequests);

        return "friendsList";
    }

}