package com.bookApp.web.bookshelf;

import com.bookApp.web.book.Book;
import com.bookApp.web.book.BookRepository;
import com.bookApp.web.book.BookSearchService;
import com.bookApp.web.book.BookService;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class BookshelfController {

    //private final BookServiceImpl bookServiceImpl;
    private final BookRepository bookRepository;
    private final BookService bookService;
    private final BookshelfRepository bookshelfRepository;
    private final BookshelfService bookshelfService;



    //constructor
    @Autowired
    public BookshelfController(BookRepository bookRepository, BookService bookService, BookshelfRepository bookshelfRepository, BookshelfService bookshelfService) {
        this.bookRepository = bookRepository;
        this.bookService = bookService;
        this.bookshelfRepository = bookshelfRepository;
        this.bookshelfService = bookshelfService;
    }

    //bookshelf page
    @GetMapping("/myBooks")
    public String listBooks(Model model){
        List<Book> books = bookService.getBook();
        model.addAttribute("books", books);

        List<Bookshelf> bookshelf = bookshelfService.getBookshelf();
        model.addAttribute("books", books);


        return "bookShelf";
    }

}
