package com.bookApp.web.controllers;

import org.springframework.ui.Model;
import com.bookApp.web.models.Book;
import com.bookApp.web.repositories.BookRepository;
import com.bookApp.web.services.impl.BookServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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
    public String listBooks(Model model){
        List<Book> books = bookServiceImpl.getBook();
        model.addAttribute("books", books);
        return "index";
    }
}
