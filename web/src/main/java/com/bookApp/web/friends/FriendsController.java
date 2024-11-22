package com.bookApp.web.friends;

import com.bookApp.web.user.User;
import com.bookApp.web.user.UserRepository;
import com.bookApp.web.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Controller
public class FriendsController {

    private final FriendsRepository friendsRepository;
    private final UserRepository userRepository;
    private final User user;
    private UserService userService;

    public FriendsController(UserService userService, FriendsRepository friendsRepository, UserRepository userRepository, User user) {
        this.userService = userService;
        this.friendsRepository = friendsRepository;
        this.userRepository = userRepository;
        this.user = user;
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

        LocalDateTime currentDate = LocalDateTime.now();

        if (friend != null) {
            if (answer.equals("Yes")) {
                friend.setStatus(Friends.Status.ACCEPTED);
            }
            else {
                friend.setStatus(Friends.Status.DECLINED);
            }
            friend.setDateUpdated(currentDate);
            friendsRepository.save(friend);
            return ResponseEntity.ok().build();
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    //change e.g. aSearch to a-search
    @GetMapping("/friends/search-json")
    @ResponseBody
    public List<Map<String, Object>> searchBooksJson(@RequestParam(value = "query") String query, Principal principal) {
        User currentUser = userService.findByUsername(principal.getName());

        List<User> users = friendsRepository.searchUsersForFriendRequest(currentUser.getId(), query);

        //create a response with a list of maps containing only the necessary fields
        return users.stream()
                .map(user -> {
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("id", user.getId());
                    userData.put("username", user.getUsername());
                    return userData;
                })
                .collect(Collectors.toList());
    }

    @PostMapping("/{userId}/send-request")
    public ResponseEntity<String> sendRequest(@PathVariable Long userId, Principal principal) {
        User sender = userService.findByUsername(principal.getName());
        User receiver = userRepository.findById(userId).orElse(null);

        LocalDateTime currentDate = LocalDateTime.now();

        if (receiver != null) {
            Friends friend = new Friends();
            friend.setSender(sender);
            friend.setReceiver(receiver);
            friend.setStatus(Friends.Status.PENDING);
            friend.setDateCreated(currentDate);
            friend.setDateUpdated(currentDate);

            friendsRepository.save(friend);
            return ResponseEntity.ok("Friend Request sent \uD83E\uDD73");
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}