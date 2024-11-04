package com.bookApp.web.friends;

import com.bookApp.web.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendsRepository extends JpaRepository<Friends, Long> {

    List<Friends> findAllBySenderOrReceiver(User sender, User receiver);

    List<Friends> findAllBySender(User sender);

    List<Friends> findAllByReceiver(User receiver);

}
