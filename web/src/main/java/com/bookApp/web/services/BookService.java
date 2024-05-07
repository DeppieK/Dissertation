package com.bookApp.web.services;

import com.bookApp.web.dto.BookDto;

import java.util.List;

public interface BookService {
    List<BookDto> findAllBooks();
    BookDto findBookById(Long bookId);
}
