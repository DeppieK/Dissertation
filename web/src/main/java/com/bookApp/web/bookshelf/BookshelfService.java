package com.bookApp.web.bookshelf;

import com.bookApp.web.book.Book;
import com.bookApp.web.user.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BookshelfService {

    private final BookshelfRepository bookshelfRepository;

    public void save(Bookshelf bookshelf) {
        bookshelfRepository.save(bookshelf);
    }
    public BookshelfService(BookshelfRepository bookshelfRepository) {
        this.bookshelfRepository = bookshelfRepository;
    }

    public List<Bookshelf> getBookshelf() {
        return bookshelfRepository.findAll();
    }

    public List<Bookshelf> getBookshelfByUser(User user) {
        return bookshelfRepository.findByUser(user);
    }

    public long countBooksByUserAndLabel(User user, String label) {
        return bookshelfRepository.countByUserAndLabel(user, label);
    }

    public List<Bookshelf> getBookshelvesWithoutSpecifiedLabels(User user) {
        return bookshelfRepository.findBookshelvesWithoutSpecifiedLabels(user);
    }

    public List<Bookshelf> getBookshelvesByUserAndLabels(User user, List<String> labels) {
        return bookshelfRepository.findByUserAndLabelIn(user, labels);
    }

    public Map<String, Long> getBookshelvesWithCountByUser(User user) {
        List<Bookshelf> bookshelves = bookshelfRepository.findBookshelvesWithoutSpecifiedLabels(user);
        return bookshelves.stream()
                .collect(Collectors.toMap(
                        Bookshelf::getLabel,
                        bookshelf -> countBooksByUserAndLabel(user, bookshelf.getLabel()),
                        (existing, replacement) -> existing // handle duplicate keys
                ));
    }

    public Long getNextShelfId() {
        Long maxShelfId = bookshelfRepository.findMaxShelfId();
        return maxShelfId + 1;
    }

}

