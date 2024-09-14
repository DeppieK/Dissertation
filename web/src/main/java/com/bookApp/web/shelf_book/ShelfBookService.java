package com.bookApp.web.shelf_book;

import com.bookApp.web.bookshelf.Bookshelf;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

@Service
public class ShelfBookService {


    private final ShelfBookRepository shelfBookRepository;

    public ShelfBookService(ShelfBookRepository shelfBookRepository) {
        this.shelfBookRepository = shelfBookRepository;
    }

    public void save(ShelfBook shelfbook) {
        shelfBookRepository.save(shelfbook);
    }
}
