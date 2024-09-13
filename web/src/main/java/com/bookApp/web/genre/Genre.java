package com.bookApp.web.genre;

import com.bookApp.web.book.Book;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@Setter
@Getter
@SpringBootApplication
@ComponentScan("com.bookApp.web")
@Entity
@Table(name = "genre_book")
public class Genre {
    @Id
    @SequenceGenerator(
            name = "genre_sequence",
            sequenceName = "genre_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "genre_sequence"
    )

    private Long id;

    private String genre;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    public Genre() {

    }

    public Genre(Long id, String genre, Book book) {
        this.id = id;
        this.genre = genre;
        this.book = book;
    }

    @Override
    public String toString() {
        return "Genre{" +
                "id=" + id +
                ", genre='" + genre + '\'' +
                ", book=" + book +
                '}';
    }
}
