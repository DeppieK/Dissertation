package com.bookApp.web.bookshelf;

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
@Table(name = "bookshelves")
public class Bookshelf {
    @Id
    @SequenceGenerator(
            name = "bookshelf_sequence",
            sequenceName = "bookshelf_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "bookshelf_sequence"
    )
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "shelf_id", nullable = false)
    private Long shelfId;

    @Column(name = "label")
    private String label;

    // Constructors
    public Bookshelf() {
    }

    public Bookshelf(Long id) {
        this.id = id;
    }

    public Bookshelf(User user, Book book, Long shelfId, String label) {
        this.user = user;
        this.shelfId = shelfId;
        this.label = label;
    }

    // toString
    @Override
    public String toString() {
        return "Bookshelf{" +
                "id=" + id +
                ", user=" + user +
                ", shelfId=" + shelfId +
                ", label='" + label + '\'' +
                '}';
    }
}
