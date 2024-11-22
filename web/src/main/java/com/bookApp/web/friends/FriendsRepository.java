package com.bookApp.web.friends;

import ch.qos.logback.core.status.Status;
import com.bookApp.web.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendsRepository extends JpaRepository<Friends, Long> {

    @Query("SELECT f FROM Friends f WHERE (f.receiver = :user OR f.sender = :user) AND f.status = :status")
    List<Friends> findAllBySenderOrReceiverAndStatus(User user, Friends.Status status);

    List<Friends> findAllBySenderAndStatus(User sender, Friends.Status status);

    List<Friends> findAllByReceiverAndStatus(User receiver, Friends.Status status);

    @Query("SELECT u FROM User u " +
            "WHERE u.id != :currentUser AND (" +
            "    NOT EXISTS (" +
            "        SELECT f FROM Friends f " +
            "        WHERE (f.sender.id = u.id AND f.receiver.id = :currentUser) " +
            "           OR (f.sender.id = :currentUser AND f.receiver.id = u.id)" +
            "    ) OR (" +
            "        EXISTS (" +
            "            SELECT f FROM Friends f " +
            "            WHERE ((f.sender.id = u.id AND f.receiver.id = :currentUser) " +
            "               OR (f.sender.id = :currentUser AND f.receiver.id = u.id)) " +
            "               AND f.status = 'DECLINED'" +
            "        )" +
            "    )" +
            ") AND u.username ILIKE CONCAT(:query, '%')")
    List<User> searchUsersForFriendRequest(@Param("currentUser") Long currentUser, @Param("query") String query);






}
