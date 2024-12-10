package com.bookApp.web.friends;

import com.bookApp.web.book.Book;
import com.bookApp.web.bookshelf.Bookshelf;
import com.bookApp.web.shelf_book.ShelfBook;
import lombok.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Component
public class FriendsReadingDto {
    private Bookshelf bookshelf;
    private Book book;
    private ShelfBook shelfBook;
    private LocalDateTime dateUpdated;

    public FriendsReadingDto(Bookshelf bookshelf, Book book) {
        this.bookshelf = bookshelf;
        this.book = book;
    }

    public FriendsReadingDto(Bookshelf bookshelf, Book book, ShelfBook shelfBook) {
        this.bookshelf = bookshelf;
        this.book = book;
        this.shelfBook = shelfBook;
    }

}
