package com.bookApp.web.bookshelf;

import com.bookApp.web.friends.FriendsReadingDto;
import com.bookApp.web.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookshelfRepository extends JpaRepository<Bookshelf, Long> {
    List<Bookshelf> findByUser(User user);

    Bookshelf findByShelfId(Long shelfId);

    @Query("SELECT COUNT(s) FROM ShelfBook s WHERE s.shelfId = :shelfId")
    long countByShelfId(Long shelfId);


    @Query("SELECT b FROM Bookshelf b WHERE b.user = :user AND b.label NOT IN ('Currently Reading', 'Read', 'Want to Read')")
    List<Bookshelf> findBookshelvesWithoutSpecifiedLabels(@Param("user") User user);

    List<Bookshelf> findByUserAndLabelIn(User user, List<String> labels);

    @Query("SELECT COALESCE(MAX(b.shelfId), 0) FROM Bookshelf b")
    Long findMaxShelfId();

    @Query("SELECT COUNT(b) FROM Bookshelf b WHERE b.label LIKE 'untitled%' AND b.user = :user")
    Long countUntitledLabelsByUser(@Param("user") User user);

    @Query("SELECT COUNT(b) FROM Bookshelf b WHERE b.label LIKE CONCAT(:labelName, '%')")
    Long countSimilarLabels(@Param("labelName") String labelName);

    Bookshelf findByUserAndLabel(User user, String label);

    @Query("SELECT COALESCE(b.shelfId, 0) FROM Bookshelf b WHERE b.user = :user AND b.label = :label")
    Long findShelfIdByUserAndLabel(@Param("user") User user, @Param("label") String label);

    @Query("SELECT new com.bookApp.web.friends.FriendsReadingDto(b, book, s, s.dateUpdated) " +
            "FROM Bookshelf b " +
            "JOIN ShelfBook s ON b.shelfId = s.shelfId " +
            "JOIN s.book book " +
            "JOIN Friends f ON (b.user = f.receiver OR b.user = f.sender) " +
            "WHERE b.label = :bookshelfLabel " +
            "AND f.status = 'ACCEPTED' " +
            "AND (f.receiver = :currentUser OR f.sender = :currentUser) " +
            "AND b.user <> :currentUser")
    List<FriendsReadingDto> getFriendsBooksWithSpecifiedLabels(
            @Param("currentUser") User currentUser,
            @Param("bookshelfLabel") String bookshelfLabel);

}
