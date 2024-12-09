package com.bookApp.web.friends;

import com.bookApp.web.book.Book;
import com.bookApp.web.bookshelf.Bookshelf;
import lombok.*;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Component
public class FriendsReadingDto {
    private Bookshelf bookshelf;
    private Book book;

}
