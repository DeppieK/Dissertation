package com.bookApp.web.services.impl;

import com.bookApp.web.dto.BookDto;
import com.bookApp.web.models.Book;
import com.bookApp.web.repositories.BookRepository;
import com.bookApp.web.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
/*
@Service
public class BookServiceImpl implements BookService {

    private BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }
    @Override
    public List<BookDto> findAllBooks() {
        List<Book> books = bookRepository.findAll();
        return books.stream().map((book) -> mapToBookDto(book)).collect(Collectors.toList());
    }

    @Override
    public BookDto findBookById(Long bookId) {
        Book book = bookRepository.findById(bookId).get();
        return mapToBookDto(book);
    }
    public static BookDto mapToBookDto(Book book) {
        BookDto bookDto = BookDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .photoUrl(book.getPhotoUrl())
                .build();
        return bookDto;
    }
    public List<Book> getBook(){
        return bookRepository.findAll();
    }
}*/
