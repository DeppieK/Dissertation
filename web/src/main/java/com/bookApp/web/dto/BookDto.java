package com.bookApp.web.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookDto {
    private Long id;
    private String title;
    private String genre;
    private String description;
    private Long ISBN;
    private int pages;
    private String author;
    private String photoUrl;
}
