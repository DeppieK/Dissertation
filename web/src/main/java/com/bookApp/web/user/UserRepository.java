package com.bookApp.web.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    Logger logger = LoggerFactory.getLogger(UserRepository.class);

    default User findByUsernameWithLogging(String username) {
        logger.info("Searching for user with username: {}", username);
        User user = findByUsername(username);
        if (user == null) {
            logger.warn("User not found with username: {}", username);
        } else {
            logger.info("User found: {}", user.getUsername());
        }
        return user;
    }
}

