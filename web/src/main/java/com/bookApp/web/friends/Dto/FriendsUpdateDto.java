package com.bookApp.web.friends.Dto;

import lombok.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Component
public class FriendsUpdateDto {
    private String type; //case type
    private String username;
    private Long bookId;
    private String bookTitle;
    private String bookPhotoUrl;
    private LocalDateTime dateUpdated;
    private double stars;
}
