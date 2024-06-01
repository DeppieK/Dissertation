package com.bookApp.web.bookshelf;

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
}
