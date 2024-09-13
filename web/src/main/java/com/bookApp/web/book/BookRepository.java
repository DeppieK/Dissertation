package com.bookApp.web.book;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {


    //the query for the search method
    @Query("SELECT b FROM Book b WHERE " +
            "LOWER(b.title) LIKE CONCAT('%', LOWER(:query), '%') OR " +
            "LOWER(b.author) LIKE CONCAT('%', LOWER(:query), '%') OR " +
            "CAST(b.ISBN AS STRING) LIKE CONCAT('%', LOWER(:query), '%')"
            )
    List<Book> searchBooks(@Param("query") String query);

    @Query("SELECT g.book FROM Genre g WHERE LOWER(g.genre) = LOWER(:genre)")
    List<Book> findByGenre(@Param("genre") String genre);

    List<Book> findByISBN(Long isbn);
    List<Book> findByAuthor(String author);
}
