package com.bookApp.web.bookshelf;

import com.bookApp.web.user.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookshelfService {

    private final BookshelfRepository bookshelfRepository;

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
}

