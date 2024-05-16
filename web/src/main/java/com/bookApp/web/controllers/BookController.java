package com.bookApp.web.controllers;

import com.bookApp.web.dto.BookDto;
import com.bookApp.web.services.BookService;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.ui.Model;
import com.bookApp.web.models.Book;
import com.bookApp.web.repositories.BookRepository;
//import com.bookApp.web.services.impl.BookServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class BookController {

    //private final BookServiceImpl bookServiceImpl;
    private final BookRepository bookRepository;
    private final BookService bookService;

    //constructor
    @Autowired
    public BookController(/*BookServiceImpl bookServiceImpl,*/ BookRepository bookRepository, BookService bookService) {
        //this.bookServiceImpl = bookServiceImpl;
        this.bookRepository = bookRepository;
        this.bookService = bookService;
    }

    //main page
    @GetMapping("/books")
    public String listBooks(Model model){
        List<Book> books = bookService.getBook();
        model.addAttribute("books", books);
        return "index";
    }

    //details page
//page with the book details
    @GetMapping("/books/{bookId}")
    public String bookDetail(@PathVariable("bookId") long bookId, Model model) throws ChangeSetPersister.NotFoundException {
        Book book = bookService.findBookById(bookId);
        //Long userId = commentsService.getUserIdByBookId(bookId); // Updated to Long

        model.addAttribute("book", book);
        //model.addAttribute("user", userId);

        return "detailsPage";
    }
}
