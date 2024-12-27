package com.bookApp.web.book;

import com.bookApp.web.bookshelf.BookshelfRepository;
import com.bookApp.web.bookshelf.BookshelfService;
import com.bookApp.web.friends.FriendsReadingDto;
import com.bookApp.web.friends.Dto.FriendsUpdateDto;
import com.bookApp.web.genre.Genre;
import com.bookApp.web.genre.GenreRepository;
import com.bookApp.web.ratings.Ratings;
import com.bookApp.web.ratings.RatingsRepository;
import com.bookApp.web.ratings.RatingsService;
import com.bookApp.web.shelf_book.ShelfBook;
import com.bookApp.web.shelf_book.ShelfBookRepository;
import com.bookApp.web.shelf_book.ShelfBookService;
import com.bookApp.web.user.User;
import com.bookApp.web.user.UserService;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class BookController {

    private final BookRepository bookRepository;
    private final BookService bookService;
    private final BookSearchService bookSearchService;
    private final RatingsRepository ratingsRepository;
    private final UserService userService;
    private final RatingsService ratingsService;
    private final GenreRepository genreRepository;
    private final BookshelfService bookshelfService;
    private final ShelfBookService shelfBookService;
    private final ShelfBookRepository shelfBookRepository;
    private final BookshelfRepository bookshelfRepository;

    //constructor
    @Autowired
    public BookController(BookRepository bookRepository, BookService bookService, BookSearchService bookSearchService, RatingsRepository ratingsRepository, UserService userService, RatingsService ratingsService, GenreRepository genreRepository, BookshelfService bookshelfService, ShelfBookService shelfBookService, ShelfBookRepository shelfBookRepository, BookshelfRepository bookshelfRepository) {
        this.bookRepository = bookRepository;
        this.bookService = bookService;
        this.bookSearchService = bookSearchService;
        this.ratingsRepository = ratingsRepository;
        this.userService = userService;
        this.ratingsService = ratingsService;
        this.genreRepository = genreRepository;
        this.bookshelfService = bookshelfService;
        this.shelfBookService = shelfBookService;
        this.shelfBookRepository = shelfBookRepository;
        this.bookshelfRepository = bookshelfRepository;
    }

    //main page
    @GetMapping("/books")
    public String listBooks(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userService.findByUsername(username);

        LocalDateTime currentDate = LocalDateTime.now();
        LocalDateTime thresholdDate = currentDate.minusDays(20);

        List<Ratings> friendsRatings = ratingsRepository.getFriendsRatingsInASpecificTimestamp(user,thresholdDate);

        //extract in a method?
        List<FriendsReadingDto> friendsCurrentlyReading = bookshelfRepository.getFriendsBooksWithSpecifiedLabels(user,thresholdDate,"Currently Reading");
        List<FriendsReadingDto> friendsRead = bookshelfRepository.getFriendsBooksWithSpecifiedLabels(user,thresholdDate,"Read");
        List<FriendsReadingDto> friendsWantToRead = bookshelfRepository.getFriendsBooksWithSpecifiedLabels(user,thresholdDate,"Want to Read");

        List<FriendsUpdateDto> allUpdates = new ArrayList<>();

        //ratings
        for (Ratings rating : friendsRatings) {
            System.out.println("Rating dateUpdated: " + rating.getDateUpdated());
            allUpdates.add(new FriendsUpdateDto(
                    "rating",
                    rating.getUser().getUsername(),
                    rating.getBook().getId(),
                    rating.getBook().getTitle(),
                    rating.getBook().getPhotoUrl(),
                    rating.getDateUpdated(),
                    rating.getStars()
            ));
        }

        //currently reading
        for (FriendsReadingDto reading : friendsCurrentlyReading) {
            System.out.println("Reading dateUpdated: " + reading.getDateUpdated());

            allUpdates.add(new FriendsUpdateDto(
                    "currentlyReading",
                    reading.getBookshelf().getUser().getUsername(),
                    reading.getBook().getId(),
                    reading.getBook().getTitle(),
                    reading.getBook().getPhotoUrl(),
                    reading.getShelfBook().getDateUpdated(),
                    0
            ));
        }

        //books read
        for (FriendsReadingDto read : friendsRead) {
            System.out.println("read dateUpdated: " + read.getDateUpdated());

            allUpdates.add(new FriendsUpdateDto(
                    "read",
                    read.getBookshelf().getUser().getUsername(),
                    read.getBook().getId(),
                    read.getBook().getTitle(),
                    read.getBook().getPhotoUrl(),
                    read.getShelfBook().getDateUpdated(),
                    0
            ));
        }

        //want to read
        for (FriendsReadingDto wantToRead : friendsWantToRead) {
            System.out.println("wantToRead dateUpdated: " + wantToRead.getDateUpdated());

            allUpdates.add(new FriendsUpdateDto(
                    "wantToRead",
                    wantToRead.getBookshelf().getUser().getUsername(),
                    wantToRead.getBook().getId(),
                    wantToRead.getBook().getTitle(),
                    wantToRead.getBook().getPhotoUrl(),
                    wantToRead.getShelfBook().getDateUpdated(),
                    0
            ));
        }

        allUpdates.sort(Comparator.comparing(FriendsUpdateDto::getDateUpdated, Comparator.nullsLast(LocalDateTime::compareTo)).reversed());

        System.out.println(allUpdates);

        model.addAttribute("allUpdates", allUpdates);

        return "index";
    }

    //details page
    @GetMapping("/books/{bookId}")
    public String bookDetail(@PathVariable("bookId") long bookId, Model model) throws ChangeSetPersister.NotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userService.findByUsername(username);

        Book book = bookService.findBookById(bookId);
        List<Ratings> ratings = ratingsRepository.findByBookId(bookId);
        List<Genre> genres = genreRepository.findByBookId(bookId);

        //check if there are any ratings
        boolean hasRatings = !ratings.isEmpty();

        double averageRating = 0.0;
        if (hasRatings) {
            double totalRating = 0;
            for (Ratings rating : ratings) {
                totalRating += rating.getStars();
            }
            averageRating = totalRating / ratings.size();
        }

        Ratings userRating = null;
        boolean userRatingExists = false;
        List<Ratings> otherRatings = new ArrayList<>();

        for (Ratings rating : ratings) {
            if (rating.getUser().getUsername().equals(username)) {
                userRating = rating;
                userRatingExists = true;
            } else {
                otherRatings.add(rating);
            }
        }

        List<Ratings> orderedRatings = new ArrayList<>();
        if (userRating != null) {
            orderedRatings.add(userRating);
        }
        orderedRatings.addAll(otherRatings);

        model.addAttribute("user", user);
        model.addAttribute("book", book);
        model.addAttribute("ratings", orderedRatings);
        model.addAttribute("genres", genres);
        model.addAttribute("userRatingExists", userRatingExists);
        model.addAttribute("userRating", userRating);
        model.addAttribute("hasRatings", hasRatings);
        model.addAttribute("averageRating", averageRating);

        return "detailsPage";
    }


    @PostMapping("/addRating")
    public String addRating(@RequestParam("bookId") long bookId, @RequestParam("rating") String rating, @RequestParam("stars") double stars, Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userService.findByUsername(username);
        Book book = bookService.findBookById(bookId);

        Ratings userRating = ratingsRepository.findByBookIdAndUserId(bookId, user.getId());
        boolean userRatingExists = false;

        if (userRating == null) {
            LocalDateTime currentDate = LocalDateTime.now();

            Ratings newRating = new Ratings();
            newRating.setUser(user);
            newRating.setBook(book);
            newRating.setDescription(rating);
            newRating.setStars(stars);
            newRating.setDateCreated(currentDate);
            newRating.setDateUpdated(currentDate);

            ratingsService.save(newRating);
        }
        else{
            userRatingExists = true;
        }

        model.addAttribute("userRatingExists", userRatingExists);
        return "redirect:/books/" + bookId;
    }

    //search method
    @GetMapping("books/search")
    public String searchBooks(@RequestParam(value = "query") String query, Model model){
        List<Book> books = bookSearchService.searchBooks(query);
        model.addAttribute("books",books);
        return "books";
    }

    @GetMapping("/books/search-json")
    @ResponseBody
    public List<Map<String, Object>> searchBooksJson(@RequestParam(value = "query") String query) {
        List<Book> books = bookSearchService.searchBooks(query);

        //create a response with a list of maps containing only the necessary fields
        return books.stream()
                .map(book -> {
                    Map<String, Object> bookData = new HashMap<>();
                    bookData.put("id", book.getId());
                    bookData.put("title", book.getTitle());
                    bookData.put("photoUrl", book.getPhotoUrl());
                    return bookData;
                })
                .collect(Collectors.toList());
    }

    //display books based on a specific genre
    @GetMapping("/books/genre/{genre}")
    public String findBooksByGenre(@PathVariable("genre") String genre, Model model) {
        List<Book> books = bookRepository.findByGenre(genre);
        model.addAttribute("books", books);
        return "genres";
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
    public String genres(Model model) {

        return "genres";
    }

    //do we like this path name?
    @PostMapping("/{label}")
    public String addToLabel(@PathVariable String label, @RequestParam("bookId") long bookId, Model model, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userService.findByUsername(username);
        Book book = bookService.findBookById(bookId);

        Long shelfId = bookshelfService.getShelfIdByUserAndLabel(user,label);

        ShelfBook shelfBookExists = shelfBookRepository.findByShelfIdAndBookId(shelfId,bookId);
        if (shelfBookExists == null){
            LocalDateTime currentDate = LocalDateTime.now();

            ShelfBook shelfBook = new ShelfBook();
            shelfBook.setShelfId(shelfId);
            shelfBook.setBook(book);
            shelfBook.setDateCreated(currentDate);
            shelfBook.setDateUpdated(currentDate);

            shelfBookService.save(shelfBook);
        }


        return "redirect:/books/" + bookId;
    }

    @GetMapping("/books/delete-rating/{ratingId}")
    @ResponseBody
    public ResponseEntity<Void> deleteRating(@PathVariable long ratingId) {

        Ratings rating =  ratingsRepository.findById(ratingId);

        if (rating != null) {
            ratingsRepository.delete(rating);
            return ResponseEntity.ok().build(); //success
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @GetMapping("/books/edit/{ratingId}")
    @ResponseBody
    public ResponseEntity<Void> editUserRating(@PathVariable long ratingId, @RequestParam("newDesc") String newDesc, @RequestParam("newStars") double newStars) {

        Ratings rating =  ratingsRepository.findById(ratingId);

        if (rating != null){
            LocalDateTime currentDate = LocalDateTime.now();

            rating.setDescription(newDesc);
            rating.setStars(newStars);
            rating.setDateUpdated(currentDate);

            ratingsService.save(rating);
            return ResponseEntity.ok().build(); //success
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

}
