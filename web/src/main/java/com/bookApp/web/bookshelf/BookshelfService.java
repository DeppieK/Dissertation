package com.bookApp.web.bookshelf;

import com.bookApp.web.user.User;
import org.springframework.stereotype.Service;

import com.bookApp.web.shelf_book.ShelfBookRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BookshelfService {

    private final BookshelfRepository bookshelfRepository;
    private final ShelfBookRepository shelfBookRepository;

    public BookshelfService(BookshelfRepository bookshelfRepository, ShelfBookRepository shelfBookRepository) {
        this.bookshelfRepository = bookshelfRepository;
        this.shelfBookRepository = shelfBookRepository;
    }

    public void save(Bookshelf bookshelf) {
        bookshelfRepository.save(bookshelf);
    }

    public List<Bookshelf> getBookshelf() {
        return bookshelfRepository.findAll();
    }

    public List<Bookshelf> getBookshelfByUser(User user) {
        return bookshelfRepository.findByUser(user);
    }

    public long countByShelfId(Long shelfId) {
        return bookshelfRepository.countByShelfId(shelfId);
    }

    public List<Bookshelf> getBookshelvesWithoutSpecifiedLabels(User user) {
        return bookshelfRepository.findBookshelvesWithoutSpecifiedLabels(user);
    }

    public List<Bookshelf> getBookshelvesByUserAndLabels(User user, List<String> labels) {
        return bookshelfRepository.findByUserAndLabelIn(user, labels);
    }

    public Bookshelf getBookshelfByUserAndLabel(User user, String label) {
        return bookshelfRepository.findByUserAndLabel(user, label);
    }

    public Long getShelfIdByUserAndLabel(User user, String label) {
        return bookshelfRepository.findShelfIdByUserAndLabel(user, label);
    }

    public Map<String, Long> getBookshelvesWithCountByUser(User user) {
        List<Bookshelf> bookshelves = bookshelfRepository.findBookshelvesWithoutSpecifiedLabels(user);
        return bookshelves.stream()
                .collect(Collectors.toMap(
                        Bookshelf::getLabel,
                        bookshelf -> countByShelfId(bookshelf.getShelfId()), // Get count by shelfId
                        (existing, replacement) -> existing //handle duplicate keys
                ));
    }

    public Long getNextShelfId() {
        Long maxShelfId = bookshelfRepository.findMaxShelfId();
        return maxShelfId + 1;
    }

}

