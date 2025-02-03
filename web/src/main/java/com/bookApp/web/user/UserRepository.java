package com.bookApp.web.user;

import com.bookApp.web.friends.Friends;
import org.springframework.data.jpa.repository.JpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsernameIgnoreCase(String username);

    Logger logger = LoggerFactory.getLogger(UserRepository.class);


    default User findByUsernameWithLogging(String username) {
        logger.info("Searching for user with username: {}", username);
        User user = findByUsernameIgnoreCase(username);
        if (user == null) {
            logger.warn("User not found with username: {}", username);
        } else {
            logger.info("User found: {}", user.getUsername());
        }
        return user;
    }

    @Query("SELECT COUNT(u) > 0 FROM User u WHERE LOWER(u.username) = LOWER(:username)")
    boolean existsByUsername(@Param("username") String username);

}

