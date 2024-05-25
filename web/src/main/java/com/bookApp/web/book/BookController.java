package com.bookApp.web.book;

import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.ui.Model;
//import com.bookApp.web.services.impl.BookServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class BookController {

    //private final BookServiceImpl bookServiceImpl;
    private final BookRepository bookRepository;
    private final BookService bookService;
    private final BookSearchService bookSearchService;


    //constructor
    @Autowired
    public BookController(/*BookServiceImpl bookServiceImpl,*/ BookRepository bookRepository, BookService bookService, BookSearchService bookSearchService) {
        //this.bookServiceImpl = bookServiceImpl;
        this.bookRepository = bookRepository;
        this.bookService = bookService;
        this.bookSearchService = bookSearchService;
    }

    //main page
    @GetMapping("/books")
    public String listBooks(Model model){
        List<Book> books = bookService.getBook();
        model.addAttribute("books", books);
        return "index";
    }

    //details page
    @GetMapping("/books/{bookId}")
    public String bookDetail(@PathVariable("bookId") long bookId, Model model) throws ChangeSetPersister.NotFoundException {
        Book book = bookService.findBookById(bookId);
        //Long userId = commentsService.getUserIdByBookId(bookId); // Updated to Long

        model.addAttribute("book", book);
        //model.addAttribute("user", userId);

        return "detailsPage";
    }

    //search method
    @GetMapping("books/search")
    public String searchBooks(@RequestParam(value = "query") String query, Model model){
        List<Book> books = bookSearchService.searchBooks(query);
        model.addAttribute("books",books);
        return "index";
    }

    //search books based on isbn
    @GetMapping("/books/isbn/{isbn}")
    public String findBooksByISBN(@PathVariable("isbn") Long isbn, Model model) {
        List<Book> books = bookRepository.findByISBN(isbn);
        model.addAttribute("books", books);
        return "index";
    }

    //display books based on a specific genre
    @GetMapping("/books/genre/{genre}")
    public String findBooksByGenre(@PathVariable("genre") String genre, Model model) {
        List<Book> books = bookRepository.findByGenre(genre);
        model.addAttribute("books", books);
        return "index";
    }

    //display books based on a specific author
    @GetMapping("/books/author/{author}")
    public String findBooksByAuthor(@PathVariable("author") String author, Model model) {
        List<Book> books = bookRepository.findByAuthor(author);
        model.addAttribute("books", books);
        return "index";
    }
}
