package com.bookApp.web.book;

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
@Table(name = "books")
public class Book {
    @Id
    @SequenceGenerator(
            name = "book_sequence",
            sequenceName = "book_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "book_sequence"
    )

    private Long id;
    private String title;
    @Column(columnDefinition = "TEXT")
    private String description;
    private Long ISBN;
    private int pages;
    private String author;
    private String photoUrl;

    //Constructors
    public Book() {

    }

    public Book(Long id, String title, String description,Long ISBN, int pages, String author, String photoUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.ISBN = ISBN;
        this.pages = pages;
        this.author = author;
        this.photoUrl = photoUrl;
    }

    public Book(String title, String description, Long ISBN, int pages, String author, String photoUrl) {
        this.title = title;
        this.description = description;
        this.ISBN = ISBN;
        this.pages = pages;
        this.author = author;
        this.photoUrl = photoUrl;

    }

    //ToString
    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", ISBN=" + ISBN +
                ", pages=" + pages +
                ", author='" + author + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                '}';
    }
}
