package com.bookApp.web.services;

import com.bookApp.web.models.Book;
import com.bookApp.web.repositories.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BookService implements BookSearchService{
    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public List<Book> searchBooks(String query) {
        List<Book> books = bookRepository.searchBooks(query);
        return books;
    }

    @Override
    public List<Book> searchBooksByISBN(Long isbn) {
        return null;
    }

    public List<Book> getBook(){
        return bookRepository.findAll();
    }

    public Book findBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + id));
    }
}