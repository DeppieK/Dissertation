package com.bookApp.web.friends;

import com.bookApp.web.user.User;
import com.bookApp.web.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;

@SpringBootApplication
@EnableScheduling
public class FriendsSchedulingTasks {

    @Autowired
    FriendsRepository friendsRepository;
    private UserService userService;


    @Scheduled(cron = "0 0 0 * * ?") //run daily at midnight
    public void cleanUpOldDeclinedRequests(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);

        LocalDate thresholdDate = LocalDate.now().minusDays(10);

        // Directly delete the declined requests
        friendsRepository.deleteDeclinedRequestsOlderThan10Days(user, thresholdDate);

    }
}
