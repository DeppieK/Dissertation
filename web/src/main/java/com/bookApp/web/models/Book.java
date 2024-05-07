package com.bookApp.web.models;

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
    //Getters and Setters
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
    private String genre;
    @Column(columnDefinition = "TEXT")
    private String description;
    private Long ISBN;
    private int pages;
    private String author;
    private String photourl;

    //Constructors
    public Book() {

    }

    public Book(Long id, String title, String genre, String description,Long ISBN, int pages, String author, String photourl) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.description = description;
        this.ISBN = ISBN;
        this.pages = pages;
        this.author = author;
        this.photourl = photourl;
    }

    public Book(String title, String genre, String description, Long ISBN, int pages, String author, String photourl) {
        this.title = title;
        this.genre = genre;
        this.description = description;
        this.ISBN = ISBN;
        this.pages = pages;
        this.author = author;
        this.photourl = photourl;

    }

    //ToString
    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", genre='" + genre + '\'' +
                ", description='" + description + '\'' +
                ", ISBN=" + ISBN +
                ", pages=" + pages +
                ", author='" + author + '\'' +
                ", photourl='" + photourl + '\'' +
                '}';
    }
}
