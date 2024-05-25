package com.bookApp.web.book;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BookSearchService {
    List<Book> searchBooks(String query);

    List<Book> searchBooksByISBN(Long isbn);
}