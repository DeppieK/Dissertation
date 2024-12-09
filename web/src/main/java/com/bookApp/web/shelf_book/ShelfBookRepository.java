package com.bookApp.web.shelf_book;

import com.bookApp.web.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShelfBookRepository extends JpaRepository<ShelfBook, Integer> {

    List<ShelfBook> findByShelfId(Long shelfId);

    ShelfBook findByShelfIdAndBookId(Long shelfId, Long bookId);


    List<ShelfBook> findAll();

    @Query("SELECT COUNT(sb) FROM ShelfBook sb WHERE sb.shelfId = :shelfId")
    Long countBooksByShelfId(@Param("shelfId") Long shelfId);

    // Custom query to count books by user
    @Query("SELECT COUNT(sb) FROM ShelfBook sb JOIN Bookshelf b ON sb.shelfId = b.shelfId WHERE b.user = :user")
    long countBooksByUser(@Param("user") User user);

    @Query("SELECT sb FROM ShelfBook sb JOIN Book b ON sb.book.id = b.id WHERE sb.shelfId = :shelfId AND " +
            "(LOWER(b.title) LIKE CONCAT('%', LOWER(:query), '%') OR " +
            "LOWER(b.author) LIKE CONCAT('%', LOWER(:query), '%') OR " +
            "CAST(b.ISBN AS STRING) LIKE CONCAT('%', LOWER(:query), '%'))")
    List<ShelfBook> searchBooksInBookshelf(@Param("shelfId") Long shelfId, @Param("query") String query);

    /*@Query("SELECT s FROM ShelfBook s " +
            "JOIN Book b ON s.book = b " +
            "JOIN Bookshelf bs ON s.shelfId = bs.shelfId " +
            "JOIN Friends f ON (bs.user = f.receiver OR bs.user = f.sender) " +
            "WHERE s.dateUpdated >= :thresholdDate " +
            "AND bs.label = :bookshelfLabel " +
            "AND f.status = 'ACCEPTED' " +
            "AND (f.receiver = :currentUser OR f.sender = :currentUser) " +
            "AND bs.user <> :currentUser")
    List<ShelfBook> getFriendsShelfBooksWithSpecifiedLabels(
            @Param("currentUser") User currentUser,
            @Param("thresholdDate") LocalDateTime thresholdDate,
            @Param("bookshelfLabel") String bookshelfLabel);
*/

}
