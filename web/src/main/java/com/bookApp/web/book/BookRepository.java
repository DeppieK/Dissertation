package com.bookApp.web.book;

import com.bookApp.web.user.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Book findById(long id);

    //the query for the search method
    @Query("SELECT b FROM Book b WHERE " +
            "LOWER(b.title) LIKE CONCAT('%', LOWER(:query), '%') OR " +
            "LOWER(b.author) LIKE CONCAT('%', LOWER(:query), '%') OR " +
            "CAST(b.ISBN AS STRING) LIKE CONCAT('%', LOWER(:query), '%')"
            )
    List<Book> searchBooks(@Param("query") String query);

    @Query("SELECT g.book FROM Genre g WHERE LOWER(g.genre) = LOWER(:genre)")
    List<Book> findByGenre(@Param("genre") String genre);

    List<Book> findByAuthor(String author);

    @Query("SELECT DISTINCT b FROM Book b " +
            "JOIN ShelfBook sb ON sb.book.id = b.id " +
            "JOIN Bookshelf bs ON bs.shelfId = sb.shelfId " +
            "JOIN Ratings r ON r.book.id = b.id AND r.user.id = bs.user.id " +
            "WHERE bs.label = 'Read' " +
            "AND bs.user IN :users " +
            "AND (r.stars >= 4) ")
    List<Book> findBooksMarkedAsReadByUsers(@Param("users") List<User> users);

    @Query("SELECT b FROM Book b " +
            "WHERE b IN (:books) " +
            "AND b.id NOT IN (" +
            "    SELECT sb.book.id FROM ShelfBook sb " +
            "    JOIN Bookshelf bs ON bs.shelfId = sb.shelfId " +
            "    WHERE bs.user = :user" +
            ")")
    List<Book> findBooksNotInUsersBookshelf(@Param("user") User user, @Param("books") List<Book> books);

    @Query("SELECT b FROM Book b " +
            "JOIN Ratings r ON r.book.id = b.id " +
            "WHERE r.user.id = :userId " +
            "AND r.stars >= 4")
    List<Book> findBooksHighlyRated(@Param("userId") Long userId);

    @Query("SELECT DISTINCT b FROM Book b " +
            "JOIN Genre g1 ON g1.book.id = b.id " +
            "JOIN Genre g2 ON g2.genre = g1.genre AND g2.book IN (:booksHighlyRated) " +
            "GROUP BY b.id " +
            "HAVING COUNT(DISTINCT g1.genre) >= 2 ")
    List<Book> findBooksWithSimilarGenres(
            @Param("booksHighlyRated") List<Book> booksHighlyRated);

    /* For future use, returns an object first row user second row book
        @Query("SELECT u, b FROM User u " +
               "JOIN Bookshelf bs ON bs.user.id = u.id " +
               "JOIN ShelfBook sb ON sb.shelfId = bs.shelfId " +
               "JOIN Book b ON sb.book.id = b.id " +
               "WHERE bs.label = 'read'")
        List<Object[]> findUsersWithReadBooks();
     */

    /*@Query("SELECT b FROM Book b WHERE b.id IN :bookIds")
    List<Book> findByBookAndSort (@Param("book") List<Book> books, Sort sort);*/
}
