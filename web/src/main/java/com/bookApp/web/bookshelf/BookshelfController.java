package com.bookApp.web.bookshelf;

import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class BookshelfController {

    //bookshelf page
    @GetMapping("/myBooks")
    public String listBooks(Model model){
        /*
        List<Book> books = bookService.getBook();
        model.addAttribute("books", books);
         */
        return "bookShelf";
    }

}
