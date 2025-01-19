package com.bookApp.web.bookshelf;

import com.bookApp.web.book.Book;
import com.bookApp.web.book.BookRepository;
import com.bookApp.web.book.BookSearchService;
import com.bookApp.web.book.BookService;
import com.bookApp.web.ratings.RatingsRepository;
import com.bookApp.web.shelf_book.ShelfBook;
import com.bookApp.web.shelf_book.ShelfBookRepository;
import com.bookApp.web.shelf_book.ShelfBookService;
import com.bookApp.web.user.User;
import com.bookApp.web.user.UserService;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

@Controller
public class BookshelfController {

    private final BookRepository bookRepository;
    private final BookService bookService;
    private final BookshelfRepository bookshelfRepository;
    private final BookshelfService bookshelfService;
    private final UserService userService;
    private final ShelfBookRepository shelfBookRepository;
    private final BookSearchService bookSearchService;
    private final ShelfBookService shelfBookService;
    private final Book book;
    private final RatingsRepository ratingsRepository;

    @Autowired
    public BookshelfController(BookRepository bookRepository, BookService bookService,
                               BookshelfRepository bookshelfRepository, BookshelfService bookshelfService,
                               UserService userService, ShelfBookRepository shelfBookRepository, BookSearchService bookSearchService, ShelfBookService shelfBookService, Book book, RatingsRepository ratingsRepository) {
        this.bookRepository = bookRepository;
        this.bookService = bookService;
        this.bookshelfRepository = bookshelfRepository;
        this.bookshelfService = bookshelfService;
        this.userService = userService;
        this.shelfBookRepository = shelfBookRepository;
        this.bookSearchService = bookSearchService;
        this.shelfBookService = shelfBookService;
        this.book = book;
        this.ratingsRepository = ratingsRepository;
    }

    //bookshelf page
    @GetMapping("/myBooks")
    public String listBooks(Model model, Principal principal) {
        //get the current user
        User user = userService.findByUsername(principal.getName());

        List<Bookshelf> bookshelves = bookshelfService.getBookshelfByUser(user);
        model.addAttribute("bookshelves", bookshelves);

        //get the user's bookshelves with specified labels
        Bookshelf currentlyReadingBookshelves = bookshelfService.getBookshelfByUserAndLabel(user,"Currently Reading");
        model.addAttribute("currentlyReadingBookshelves", currentlyReadingBookshelves);

        Bookshelf readBookshelves = bookshelfService.getBookshelfByUserAndLabel(user,"Read");
        model.addAttribute("readBookshelves", readBookshelves);

        Bookshelf wantToReadBookshelves = bookshelfService.getBookshelfByUserAndLabel(user,"Want to Read");
        model.addAttribute("wantToReadBookshelves", wantToReadBookshelves);

        //get the user's bookshelves without specified labels
        List<Bookshelf> otherBookshelves = bookshelfService.getBookshelvesWithoutSpecifiedLabels(user);
        model.addAttribute("otherBookshelves", otherBookshelves);

        // Get the user's bookshelves without specified labels
        Map<String, Long> otherBookshelvesWithCount = bookshelfService.getBookshelvesWithCountByUser(user);
        model.addAttribute("otherBookshelvesWithCount", otherBookshelvesWithCount);

        //calculate the number of books per shelf label
        long currentlyReadingCount = bookshelfService.countByShelfId(bookshelfService.getShelfIdByUserAndLabel(user,"Currently Reading"));
        long readCount = bookshelfService.countByShelfId(bookshelfService.getShelfIdByUserAndLabel(user,"Read"));
        long wantToReadCount = bookshelfService.countByShelfId(bookshelfService.getShelfIdByUserAndLabel(user,"Want to Read"));

        model.addAttribute("currentlyReadingCount", currentlyReadingCount);
        model.addAttribute("readCount", readCount);
        model.addAttribute("wantToReadCount", wantToReadCount);

        return "bookshelf";
    }

    @PostMapping("/addShelf")
    public String addShelf(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());

        Long untitledNumber = bookshelfRepository.countUntitledLabels() + 1L;
        Bookshelf bookshelf = new Bookshelf();
        bookshelf.setLabel("untitled " + untitledNumber);
        bookshelf.setShelfId(bookshelfService.getNextShelfId());
        bookshelf.setUser(user);

        bookshelfService.save(bookshelf);
        return "redirect:/myBookshelf/untitled " + untitledNumber;
    }

    //bookshelf details
    @GetMapping("/myBookshelf/{label}")
    public String myBookshelf(@PathVariable("label") String label, Model model, Principal principal) throws ChangeSetPersister.NotFoundException {

        //get the current user
        User user = userService.findByUsername(principal.getName());

        //get the user's bookshelves
        Bookshelf bookshelves = bookshelfService.getBookshelfByUserAndLabel(user,label);
        model.addAttribute("label", label);

        Long shelfId = bookshelves.getShelfId();

        List<ShelfBook> shelfBooks = shelfBookRepository.findByShelfId(shelfId);

        Map<Long, Double> bookRatings = new HashMap<>();

        for (ShelfBook shelfBook : shelfBooks) {
            Long bookId = shelfBook.getBook().getId();
            Double averageRating = ratingsRepository.findAverageRatingForBookId(bookId);
            bookRatings.put(bookId, (averageRating != null) ? averageRating : 0.0);
        }

        model.addAttribute("bookRatings", bookRatings);
        model.addAttribute("shelfBooks", shelfBooks);

        return "bookshelfDetails";

    }

    @PostMapping("/{label}/add")
    public ResponseEntity<String> addBookToShelf(@PathVariable String label, @RequestBody Map<String, Long> requestData) {
        //get the current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);

        //find the book by id
        Long bookId = requestData.get("bookId");
        Book book = bookService.findBookById(bookId);

        //get the shelf id for the user and label
        Long shelfId = bookshelfService.getShelfIdByUserAndLabel(user, label);

        ShelfBook shelfBookExists = shelfBookRepository.findByShelfIdAndBookId(shelfId,bookId);
        if (shelfBookExists == null){
            LocalDateTime currentDate = LocalDateTime.now();
            //create and save the ShelfBook entry
            ShelfBook shelfBook = new ShelfBook();
            shelfBook.setShelfId(shelfId);
            shelfBook.setBook(book);
            shelfBook.setDateCreated(currentDate);
            shelfBook.setDateUpdated(currentDate);
            shelfBookService.save(shelfBook);

            //return a success response
            return ResponseEntity.ok("Book added to the shelf successfully!");
        }
        else{
            return ResponseEntity.ok("Book already exists in the shelf!");
        }
    }

    //change mapping? why books/....
    // bookshelf search method
    @GetMapping("books/myBookshelf/{label}/bookshelf-search")
    public String bookshelfSearchBooks(@PathVariable("label") String label, @RequestParam(value = "query") String query, Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());

        Long shelfId = bookshelfService.getShelfIdByUserAndLabel(user, label);

        List<ShelfBook> shelfBooks = shelfBookRepository.searchBooksInBookshelf(shelfId, query);

        model.addAttribute("shelfBooks", shelfBooks);
        model.addAttribute("label", label);
        return "bookshelfDetails";
    }

    @GetMapping("/bookshelf/rename/{label}")
    @ResponseBody
    public ResponseEntity<String> renameBookshelfJson(@PathVariable("label") String label, @RequestParam("newLabel") String newLabel, Principal principal) {

        User user = userService.findByUsername(principal.getName());
        Long shelfId = bookshelfService.getShelfIdByUserAndLabel(user, label);
        Bookshelf bookshelf = bookshelfRepository.findByShelfId(shelfId);
        Bookshelf labelNameExists = bookshelfRepository.findByUserAndLabel(user, newLabel);

        if (bookshelf != null) {
            String finalLabel;
            if (labelNameExists == null) {
                finalLabel = newLabel;
            } else {
                Long labelNumber = bookshelfRepository.countSimilarLabels(newLabel) + 1L;
                finalLabel = newLabel + " (" + labelNumber + ")";
            }
            bookshelf.setLabel(finalLabel);
            bookshelfService.save(bookshelf);

            //return the final label after renaming
            return ResponseEntity.ok(finalLabel);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @GetMapping("/bookshelf/delete/book/{bookId}")
    @ResponseBody
    public ResponseEntity<Void> deleteBookFromBookshelf(@PathVariable("bookId") Long bookId, @RequestParam("label") String label, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        System.out.println("Book ID: " + bookId);
        Long shelfId = bookshelfService.getShelfIdByUserAndLabel(user, label);

        ShelfBook shelfBook = shelfBookRepository.findByShelfIdAndBookId(shelfId,bookId);

        if (shelfBook != null){
           shelfBookRepository.delete(shelfBook);
            return ResponseEntity.ok().build(); //success
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @DeleteMapping("/bookshelf/delete/{bookshelfLabel}")
    @ResponseBody
    public ResponseEntity<Void> deleteBookshelf(@PathVariable("bookshelfLabel") String bookshelfLabel, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        Bookshelf bookshelf = bookshelfRepository.findByUserAndLabel(user, bookshelfLabel);
        if (bookshelf != null) {
            Long shelfId = bookshelf.getShelfId();
            //batch delete associated books
            shelfBookRepository.deleteAllByShelfIdInBatch(shelfId);
            //delete the bookshelf
            bookshelfRepository.delete(bookshelf);

            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @GetMapping("/sortByDateAsc")
    public String sortByDateAsc(@RequestParam String label, Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());

        Long shelfId = bookshelfRepository.findShelfIdByUserAndLabel(user,label);
        List<ShelfBook> sortedShelfBooks = shelfBookRepository.findByShelfIdAndSort(shelfId, Sort.by(Sort.Direction.ASC,"dateCreated"));

        //extract in a  method?
        Map<Long, Double> bookRatings = new HashMap<>();

        for (ShelfBook shelfBook : sortedShelfBooks) {
            Long bookId = shelfBook.getBook().getId();
            Double averageRating = ratingsRepository.findAverageRatingForBookId(bookId);
            bookRatings.put(bookId, (averageRating != null) ? averageRating : 0.0);
        }

        model.addAttribute("shelfBooks", sortedShelfBooks);
        model.addAttribute("label", label);
        model.addAttribute("bookRatings", bookRatings);

        return "bookshelfDetails";
    }

    @GetMapping("/sortByDateDesc")
    public String sortByDateDesc(@RequestParam String label, Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());

        Long shelfId = bookshelfRepository.findShelfIdByUserAndLabel(user,label);
        List<ShelfBook> sortedShelfBooks = shelfBookRepository.findByShelfIdAndSort(shelfId, Sort.by(Sort.Direction.DESC,"dateCreated"));

        Map<Long, Double> bookRatings = new HashMap<>();

        for (ShelfBook shelfBook : sortedShelfBooks) {
            Long bookId = shelfBook.getBook().getId();
            Double averageRating = ratingsRepository.findAverageRatingForBookId(bookId);
            bookRatings.put(bookId, (averageRating != null) ? averageRating : 0.0);
        }

        model.addAttribute("shelfBooks", sortedShelfBooks);
        model.addAttribute("label", label);
        model.addAttribute("bookRatings", bookRatings);


        return "bookshelfDetails";
    }

    @GetMapping("/sortByRatingAsc")
    public String sortByRatingAsc(@RequestParam String label, Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());

        Long shelfId = bookshelfRepository.findShelfIdByUserAndLabel(user,label);
        List<ShelfBook> sortedShelfBooks = shelfBookRepository.findByShelfId(shelfId);
        Map<Long, Double> bookRatings = new HashMap<>();

        for (ShelfBook shelfBook : sortedShelfBooks) {
            Long bookId = shelfBook.getBook().getId();
            Double averageRating = ratingsRepository.findAverageRatingForBookId(bookId);
            bookRatings.put(bookId, (averageRating != null) ? averageRating : 0.0);
        }

        sortedShelfBooks.sort(Comparator.comparing(shelfBook -> bookRatings.get(shelfBook.getBook())));

        model.addAttribute("shelfBooks", sortedShelfBooks);
        model.addAttribute("label", label);
        model.addAttribute("bookRatings", bookRatings);

        return "bookshelfDetails";
    }

    @GetMapping("/sortByRatingDesc")
    public String sortByRatingDesc(@RequestParam String label, Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());

        Long shelfId = bookshelfRepository.findShelfIdByUserAndLabel(user,label);
        List<ShelfBook> sortedShelfBooks = shelfBookRepository.findByShelfId(shelfId);
        Map<Long, Double> bookRatings = new HashMap<>();

        for (ShelfBook shelfBook : sortedShelfBooks) {
            Long bookId = shelfBook.getBook().getId();
            Double averageRating = ratingsRepository.findAverageRatingForBookId(bookId);
            bookRatings.put(bookId, (averageRating != null) ? averageRating : 0.0);
        }

        sortedShelfBooks.sort(Comparator.comparing(shelfBook -> bookRatings.get(shelfBook.getBook()), Comparator.reverseOrder()));

        model.addAttribute("shelfBooks", sortedShelfBooks);
        model.addAttribute("label", label);
        model.addAttribute("bookRatings", bookRatings);

        return "bookshelfDetails";
    }


}
