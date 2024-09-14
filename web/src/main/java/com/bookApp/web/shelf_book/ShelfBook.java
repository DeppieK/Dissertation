package com.bookApp.web.shelf_book;

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
@Table(name = "shelf_book")
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

    public ShelfBook() {
    }

    public ShelfBook(Long id, Long shelfId, Book book) {
        this.id = id;
        this.shelfId = shelfId;
        this.book = book;
    }

    @Override
    public String toString() {
        return "ShelfBook{" +
                "id=" + id +
                ", shelfId=" + shelfId +
                ", book=" + book +
                '}';
    }
}
