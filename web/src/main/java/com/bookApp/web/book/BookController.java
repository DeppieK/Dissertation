package com.bookApp.web.book;

import com.bookApp.web.bookshelf.Bookshelf;
import com.bookApp.web.bookshelf.BookshelfRepository;
import com.bookApp.web.bookshelf.BookshelfService;
import com.bookApp.web.friends.Friends;
import com.bookApp.web.friends.Dto.FriendsUpdateDto;
import com.bookApp.web.friends.FriendsRepository;
import com.bookApp.web.friends.FriendsService;
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
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.text.DecimalFormat;
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
    private final Book book;
    private final FriendsRepository friendsRepository;
    private final FriendsService friendsService;

    //constructor
    @Autowired
    public BookController(BookRepository bookRepository, BookService bookService, BookSearchService bookSearchService, RatingsRepository ratingsRepository,
                          UserService userService, RatingsService ratingsService, GenreRepository genreRepository, BookshelfService bookshelfService,
                          ShelfBookService shelfBookService, ShelfBookRepository shelfBookRepository, BookshelfRepository bookshelfRepository, Book book,
                          FriendsRepository friendsRepository, FriendsService friendsService) {
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
        this.book = book;
        this.friendsRepository = friendsRepository;
        this.friendsService = friendsService;
    }

    //main page
    @GetMapping("/books")
    public String listBooks(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);

        int requestsNotifications = friendsRepository.friendRequestsCount(user);
        List<FriendsUpdateDto> allUpdates = friendsService.fetchFriendsUpdates(user);

        Map<Long, String> bookRatings = new HashMap<>();
        DecimalFormat df = new DecimalFormat("#.##");

        for (FriendsUpdateDto update : allUpdates) {
            long bookId = update.getBookId();
            Double averageRating = ratingsRepository.findAverageRatingForBookId(bookId);
            String formattedRating = (averageRating != null) ? df.format(averageRating) : "0.00";
            bookRatings.put(bookId, formattedRating);
        }

        Map<Long, Double> currentUserRatings = new HashMap<>();
        for (FriendsUpdateDto update : allUpdates) {
            long bookId = update.getBookId();
            Ratings rating = ratingsRepository.findByBookIdAndUserId(bookId, user.getId());
            currentUserRatings.put(bookId, (rating != null) ? rating.getStars() : 0.0);
        }

        model.addAttribute("bookRatings", bookRatings);
        model.addAttribute("allUpdates", allUpdates);
        model.addAttribute("currentUserRatings", currentUserRatings);
        model.addAttribute("requestsNotifications", requestsNotifications);

        return "index";
    }

    @GetMapping("/books/api")
    @ResponseBody
    public Map<String, Object> getFriendsUpdate(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);

        List<FriendsUpdateDto> allUpdates = friendsService.fetchFriendsUpdates(user);
        DecimalFormat df = new DecimalFormat("#.##");

        Map<Long, String> bookRatings = new HashMap<>();
        Map<Long, Double> currentUserRatings = new HashMap<>();

        for (FriendsUpdateDto update : allUpdates) {
            long bookId = update.getBookId();
            Double avgRating = ratingsRepository.findAverageRatingForBookId(bookId);
            bookRatings.put(bookId, (avgRating != null) ? df.format(avgRating) : "0.00");

            Ratings rating = ratingsRepository.findByBookIdAndUserId(bookId, user.getId());
            currentUserRatings.put(bookId, (rating != null) ? rating.getStars() : 0.0);
        }

        //pageable response
        int start = Math.min(page * size, allUpdates.size());
        int end = Math.min(start + size, allUpdates.size());
        List<FriendsUpdateDto> paginatedUpdates = allUpdates.subList(start, end);

        Map<String, Object> response = new HashMap<>();
        response.put("updates", new PageImpl<>(paginatedUpdates, PageRequest.of(page, size), allUpdates.size()));
        response.put("bookRatings", bookRatings);
        response.put("currentUserRatings", currentUserRatings);

        return response;
    }

    //details page
    @GetMapping("/books/{bookId}")
    public String bookDetail(@PathVariable("bookId") long bookId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "4") int size , Model model) throws ChangeSetPersister.NotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userService.findByUsername(username);

        int requestsNotifications = friendsRepository.friendRequestsCount(user);

        Book book = bookService.findBookById(bookId);
        List<Ratings> ratings = ratingsRepository.findByBookId(bookId);
        List<Genre> genres = genreRepository.findByBookId(bookId);

        AverageRating averageRating = getAverageRating(ratings);

        //change average rating to use the repository method
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

        List<Bookshelf> otherBookshelves = bookshelfRepository.findBookshelvesWithoutSpecifiedLabels(user);
        Page<Ratings> ratingsPage = ratingsRepository.findByBookId(bookId,PageRequest.of(page, size));

        model.addAttribute("user", user);
        model.addAttribute("book", book);
        model.addAttribute("ratings", orderedRatings);
        model.addAttribute("genres", genres);
        model.addAttribute("userRatingExists", userRatingExists);
        model.addAttribute("userRating", userRating);
        model.addAttribute("hasRatings", averageRating.hasRatings());
        model.addAttribute("averageRating", averageRating.averageRating());
        model.addAttribute("otherBookshelves", otherBookshelves);
        model.addAttribute("requestsNotifications", requestsNotifications);
        model.addAttribute("ratings", ratingsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", ratingsPage.getTotalPages());

        return "detailsPage";
    }

    private static AverageRating getAverageRating(List<Ratings> ratings) {
        //check if there are any ratings
        boolean hasRatings = !ratings.isEmpty();
        double average = 0.0;

        if (hasRatings) {
            double totalRating = 0;
            for (Ratings rating : ratings) {
                totalRating += rating.getStars();
            }
            average = totalRating / ratings.size();
        }

        //format value to 2 decimal digits
        DecimalFormat df = new DecimalFormat("#.##");
        double formattedAverage = Double.parseDouble(df.format(average));

        return new AverageRating(hasRatings, formattedAverage);
    }

    private record AverageRating(boolean hasRatings, double averageRating) {
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
    public String findBooksByGenre(@PathVariable("genre") String genre, Model model,Principal principal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        int requestsNotifications = friendsRepository.friendRequestsCount(user);

        List<Book> books = bookRepository.findByGenre(genre);

        model.addAttribute("books", books);
        model.addAttribute("requestsNotifications", requestsNotifications);

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
    public String genres(Model model, Principal principal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);

        int requestsNotifications = friendsRepository.friendRequestsCount(user);
        model.addAttribute("requestsNotifications", requestsNotifications);

        return "genres";
    }

    //do we like this path name?
    @PostMapping("/{label}")
    public String addToLabel(@PathVariable String label, @RequestParam("bookId") long bookId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userService.findByUsername(username);
        Book book = bookService.findBookById(bookId);

        Long shelfId = bookshelfService.getShelfIdByUserAndLabel(user,label);

        ShelfBook shelfBookExists = shelfBookRepository.findByShelfIdAndBookId(shelfId,bookId);
        if (shelfBookExists == null){
            ShelfBook shelfBook = new ShelfBook();

            if (label.equals("Currently Reading")){
                shelfBook.setPageNumber(0);
            }

            LocalDateTime currentDate = LocalDateTime.now();

            shelfBook.setShelfId(shelfId);
            shelfBook.setBook(book);
            shelfBook.setDateCreated(currentDate);
            shelfBook.setDateUpdated(currentDate);

            shelfBookService.save(shelfBook);
        }


        return "redirect:/books/" + bookId;
    }

    //consistent mappings
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

    @GetMapping("/discoverBooks")
    public String discoverBooks(Principal principal, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userService.findByUsername(username);
        int requestsNotifications = friendsRepository.friendRequestsCount(user);

        List<User> friends = friendsRepository.findConnectedUsersByStatus(user, Friends.Status.ACCEPTED);
        List<Book> booksFriendsRead = bookRepository.findBooksMarkedAsReadByUsers(friends);
        List<Book> booksHighlyRated = bookRepository.findBooksHighlyRated(user.getId());
        List<Book> booksWithSimilarGenres = bookRepository.findBooksWithSimilarGenres(booksHighlyRated);
        //combine booksFriendsRead and booksWithSimilarGenres avoiding duplicates
        Set<Book> combinedBooks = new HashSet<>(booksFriendsRead);
        combinedBooks.addAll(booksWithSimilarGenres);

        List<Book> finalList = bookRepository.findBooksNotInUsersBookshelf(user, new ArrayList<>(combinedBooks));

        Map<Book, String> bookRatings = new HashMap<>();
        DecimalFormat df = new DecimalFormat("#.##");

        for (Book book : finalList) {
            Double averageRating = ratingsRepository.findAverageRatingForBookId(book.getId());
            String formattedRating = (averageRating != null) ? df.format(averageRating) : "0.00";
            bookRatings.put(book, formattedRating);
        }

        model.addAttribute("books", finalList);
        model.addAttribute("bookRatings", bookRatings);
        model.addAttribute("requestsNotifications", requestsNotifications);

        return "discoverBooks";
    }

}
