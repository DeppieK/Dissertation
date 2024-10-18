package com.bookApp.web.bookshelf;

import com.bookApp.web.book.Book;
import com.bookApp.web.book.BookRepository;
import com.bookApp.web.book.BookSearchService;
import com.bookApp.web.book.BookService;
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

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Autowired
    public BookshelfController(BookRepository bookRepository, BookService bookService,
                               BookshelfRepository bookshelfRepository, BookshelfService bookshelfService,
                               UserService userService, ShelfBookRepository shelfBookRepository, BookSearchService bookSearchService, ShelfBookService shelfBookService) {
        this.bookRepository = bookRepository;
        this.bookService = bookService;
        this.bookshelfRepository = bookshelfRepository;
        this.bookshelfService = bookshelfService;
        this.userService = userService;
        this.shelfBookRepository = shelfBookRepository;
        this.bookSearchService = bookSearchService;
        this.shelfBookService = shelfBookService;
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

        //create and save the ShelfBook entry
        ShelfBook shelfBook = new ShelfBook();
        shelfBook.setShelfId(shelfId);
        shelfBook.setBook(book);
        shelfBookService.save(shelfBook);

        //return a success response
        return ResponseEntity.ok("Book added to the shelf successfully!");
    }

    // bookshelf search method
    @GetMapping("books/myBookshelf/{label}/bookshelf-search")
    public String bookshelfSearchBooks(@PathVariable("label") String label, @RequestParam(value = "query") String query, Model model, Principal principal){
        User user = userService.findByUsername(principal.getName());

        Long shelfId = bookshelfService.getShelfIdByUserAndLabel(user, label);

        System.out.println("Search query: " + query);
        System.out.println("Shelf ID: " + shelfId);

        List<ShelfBook> shelfBooks = shelfBookRepository.searchBooksInBookshelf(shelfId, query);

        model.addAttribute("shelfBooks", shelfBooks);
        model.addAttribute("label", label);
        return "bookshelfDetails";
    }

    @GetMapping("/bookshelf/rename/{label}")
    @ResponseBody
    public ResponseEntity<Void> renameBookshelfJson(@PathVariable("label") String label, @RequestParam("newLabel") String newLabel,Principal principal) {
        User user = userService.findByUsername(principal.getName());

        Long shelfId = bookshelfService.getShelfIdByUserAndLabel(user, label);

        Bookshelf bookshelf = bookshelfRepository.findByShelfId(shelfId);

        if (bookshelf != null){
            bookshelf.setLabel(newLabel);
            bookshelfService.save(bookshelf);
            return ResponseEntity.ok().build(); //success
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

}
