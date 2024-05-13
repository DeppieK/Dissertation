package com.bookApp.web.controllers;

import com.bookApp.web.dto.BookDto;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.ui.Model;
import com.bookApp.web.models.Book;
import com.bookApp.web.repositories.BookRepository;
import com.bookApp.web.services.impl.BookServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class BookController {

    private final BookServiceImpl bookServiceImpl;
    private final BookRepository bookRepository;

    //constructor
    @Autowired
    public BookController(BookServiceImpl bookServiceImpl, BookRepository bookRepository) {
        this.bookServiceImpl = bookServiceImpl;
        this.bookRepository = bookRepository;
    }

    //main page
    @GetMapping("/books")
    //@ModelAttribute
    public String Books(Model model){
        List<Book> books = bookServiceImpl.getBook();
        model.addAttribute("books", books);
        return "index";
    }

    //details page
    @GetMapping("/books/{bookId}")
    //@ModelAttribute
    public String bookDetail(@PathVariable("bookId") long bookId, Model model) throws ChangeSetPersister.NotFoundException {
        BookDto bookDto = bookServiceImpl.findBookById(bookId);
        //Long userId = commentsService.getUserIdByBookId(bookId); // Updated to Long

        //List<Comments> comments = commentsService.getCommentsByBookId(bookId);

        model.addAttribute("book", bookDto);
        //model.addAttribute("comments", comments);
        //model.addAttribute("user", userId);

        return "detailsPage";
    }
}
