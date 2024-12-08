package com.bookApp.web.ratings;

import com.bookApp.web.book.Book;
import com.bookApp.web.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.time.LocalDateTime;

@Setter
@Getter
@SpringBootApplication
@ComponentScan("com.bookApp.web")
@Entity
@Table(name = "ratings", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "book_id"}))
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

    // Changed stars from int to double
    private double stars;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDateTime dateCreated;

    @Column(nullable = false)
    private LocalDateTime dateUpdated;

    public Ratings() {
    }

    public Ratings(Long id) {
        this.id = id;
    }

    public Ratings(Long id, User user, Book book, double stars, String description, LocalDateTime dateCreated, LocalDateTime dateUpdated) {
        this.id = id;
        this.user = user;
        this.book = book;
        this.stars = stars;
        this.description = description;
        this.dateCreated = dateCreated;
        this.dateUpdated = dateUpdated;
    }

    @Override
    public String toString() {
        return "Ratings{" +
                "id=" + id +
                ", user=" + user +
                ", book=" + book +
                ", stars=" + stars +
                ", description='" + description + '\'' +
                ", dateCreated=" + dateCreated +
                ", dateUpdated=" + dateUpdated +
                '}';
    }

    public String getStarsAsEmoji() {
        StringBuilder starBuilder = new StringBuilder();
        int fullStars = (int) stars;
        double remainder = stars - fullStars;
        String fullStar = "⭐";
        String halfStarImg =  "✨";

        //append full stars
        for (int i = 0; i < fullStars; i++) {
            starBuilder.append(fullStar);
        }

        //append a half star if needed
        if (remainder >= 0.5) {
            starBuilder.append(halfStarImg);
        }

        return starBuilder.toString();
    }
}
//★☆★★
