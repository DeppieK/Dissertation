package com.bookApp.web.ratings;

import com.bookApp.web.book.Book;
import com.bookApp.web.user.User;
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
@Table(name = "ratings")
public class Ratings {

    @Id
    @SequenceGenerator(
            name = "rating_sequence",
            sequenceName = "rating_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "rating_sequence"
    )
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    private int stars;
    private String description;

    public Ratings() {
    }

    public Ratings(Long id) {
        this.id = id;
    }

    public Ratings(Long id, User user, Book book, int stars, String description) {
        this.id = id;
        this.user = user;
        this.book = book;
        this.stars = stars;
        this.description = description;
    }

    @Override
    public String toString() {
        return "Ratings{" +
                "id=" + id +
                ", user=" + user +
                ", book=" + book +
                ", stars=" + stars +
                ", description=" + description +
                '}';
    }

    public String getStarsAsEmoji() {
        return "⭐".repeat(stars);
    }
}