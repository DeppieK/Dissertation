package com.bookApp.web.shelf_book;

import com.bookApp.web.book.Book;
import com.bookApp.web.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SpringBootApplication
@ComponentScan("com.bookApp.web")
@Entity
@Table(name = "shelf_book", uniqueConstraints = @UniqueConstraint(columnNames = {"shelf_id", "book_id"}))
public class ShelfBook{
    @Id
    @SequenceGenerator(
            name = "shelfBook_sequence",
            sequenceName = "shelfBook_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "shelfBook_sequence"
    )

    private Long id;

    private Long shelfId;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @Column(nullable = false)
    private LocalDateTime dateCreated;

    @Column(nullable = false)
    private LocalDateTime dateUpdated;

    @Override
    public String toString() {
        return "ShelfBook{" +
                "id=" + id +
                ", shelfId=" + shelfId +
                ", book=" + book +
                ", dateCreated=" + dateCreated +
                ", dateUpdated=" + dateUpdated +
                '}';
    }
}
