package com.bookApp.web.bookshelf;

import com.bookApp.web.book.Book;
import com.bookApp.web.book.BookRepository;
import com.bookApp.web.book.BookService;
import com.bookApp.web.user.User;
import com.bookApp.web.user.UserService;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import java.security.Principal;
import java.util.List;

@Controller
public class BookshelfController {

    private final BookRepository bookRepository;
    private final BookService bookService;
    private final BookshelfRepository bookshelfRepository;
    private final BookshelfService bookshelfService;
    private final UserService userService;

    @Autowired
    public BookshelfController(BookRepository bookRepository, BookService bookService,
                               BookshelfRepository bookshelfRepository, BookshelfService bookshelfService,
                               UserService userService) {
        this.bookRepository = bookRepository;
        this.bookService = bookService;
        this.bookshelfRepository = bookshelfRepository;
        this.bookshelfService = bookshelfService;
        this.userService = userService;
    }

    //bookshelf page
    @GetMapping("/myBooks")
    public String listBooks(Model model, Principal principal) {
        //get the current user
        User user = userService.findByUsername(principal.getName());

        //calculate the number of books per shelf label
        long currentlyReadingCount = bookshelfService.countBooksByUserAndLabel(user, "currently_reading");
        long toReadCount = bookshelfService.countBooksByUserAndLabel(user, "books_read");
        long wantToReadCount = bookshelfService.countBooksByUserAndLabel(user, "want_to_read");

        model.addAttribute("currentlyReadingCount", currentlyReadingCount);
        model.addAttribute("toReadCount", toReadCount);
        model.addAttribute("wantToReadCount", wantToReadCount);

        return "bookShelf";
    }
}
