package com.bookApp.web.friends;

import com.bookApp.web.user.User;
import com.bookApp.web.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
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

    @GetMapping("/deleteRecord/{friendId}")
    public ResponseEntity<String> deleteFriendRecord(@PathVariable Long friendId) {

        Friends friend = friendsRepository.findById(friendId).orElse(null);

        if (friend != null) {
            friendsRepository.deleteById(friendId);
            return ResponseEntity.ok().build();
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/answerRequest/{friendId}/{answer}")
    public ResponseEntity<String> answerFriendRequest(@PathVariable Long friendId, @PathVariable String answer) {

        Friends friend = friendsRepository.findById(friendId).orElse(null);

        if (friend != null) {
            if (answer.equals("Yes")) {
                friend.setStatus(Friends.Status.ACCEPTED);
                friendsRepository.save(friend);
                return ResponseEntity.ok().build();
            }
            else {
                friend.setStatus(Friends.Status.DECLINED);
                friendsRepository.save(friend);
                return ResponseEntity.ok().build();
            }
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}