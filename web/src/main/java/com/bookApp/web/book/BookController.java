package com.bookApp.web.book;

import com.bookApp.web.genre.Genre;
import com.bookApp.web.genre.GenreRepository;
import com.bookApp.web.ratings.Ratings;
import com.bookApp.web.ratings.RatingsRepository;
import com.bookApp.web.ratings.RatingsService;
import com.bookApp.web.user.User;
import com.bookApp.web.user.UserService;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class BookController {

    private final BookRepository bookRepository;
    private final BookService bookService;
    private final BookSearchService bookSearchService;
    private final RatingsRepository ratingsRepository;
    private final UserService userService;
    private final RatingsService ratingsService;
    private final GenreRepository genreRepository;

    //constructor
    @Autowired
    public BookController(BookRepository bookRepository, BookService bookService, BookSearchService bookSearchService, RatingsRepository ratingsRepository, UserService userService, RatingsService ratingsService, GenreRepository genreRepository) {
        this.bookRepository = bookRepository;
        this.bookService = bookService;
        this.bookSearchService = bookSearchService;
        this.ratingsRepository = ratingsRepository;
        this.userService = userService;
        this.ratingsService = ratingsService;
        this.genreRepository = genreRepository;
    }

    //main page
    @GetMapping("/books")
    public String listBooks(Model model){
        List<Book> books = bookService.getBook();
        model.addAttribute("books", books);
        return "books";
    }

    //details page
    @GetMapping("/books/{bookId}")
    public String bookDetail(@PathVariable("bookId") long bookId, Model model) throws ChangeSetPersister.NotFoundException {
        Book book = bookService.findBookById(bookId);
        //Long userId = commentsService.getUserIdByBookId(bookId);
        List<Ratings> ratings = ratingsRepository.findByBookId(bookId);
        List<Genre> genres = genreRepository.findByBookId(bookId);

        model.addAttribute("book", book);
        model.addAttribute("ratings", ratings);
        model.addAttribute("genres", genres);
        //model.addAttribute("user", userId);

        return "detailsPage";
    }

    @PostMapping("/addRating")
    public String addRating(@RequestParam("bookId") long bookId, @RequestParam("rating") String rating, @RequestParam("stars") double stars){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userService.findByUsername(username);
        Book book = bookService.findBookById(bookId);

        Ratings newRating = new Ratings();
        newRating.setUser(user);
        newRating.setBook(book);
        newRating.setDescription(rating);
        newRating.setStars(stars);

        ratingsService.save(newRating);

        return "redirect:/books/" + bookId;
    }

    //search method
    @GetMapping("books/search")
    public String searchBooks(@RequestParam(value = "query") String query, Model model){
        List<Book> books = bookSearchService.searchBooks(query);
        model.addAttribute("books",books);
        return "books";
    }


    //display books based on a specific genre
    @GetMapping("/books/genre/{genre}")
    public String findBooksByGenre(@PathVariable("genre") String genre, Model model) {
        List<Book> books = bookRepository.findByGenre(genre);
        model.addAttribute("books", books);
        return "books";
    }

    //display books based on a specific author
    @GetMapping("/books/author/{author}")
    public String findBooksByAuthor(@PathVariable("author") String author, Model model) {
        List<Book> books = bookRepository.findByAuthor(author);
        model.addAttribute("books", books);
        return "books";
    }

    //genres page
    @GetMapping("/genres")
    public String genres() {

        return "genres";
    }
}
