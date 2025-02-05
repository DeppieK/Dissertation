package com.bookApp.web.friends;

import com.bookApp.web.bookshelf.BookshelfRepository;
import com.bookApp.web.friends.Dto.FriendsUpdateDto;
import com.bookApp.web.ratings.Ratings;
import com.bookApp.web.ratings.RatingsRepository;
import com.bookApp.web.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import java.time.LocalDateTime;


@Service
public class FriendsService {
    @Autowired
    private RatingsRepository ratingsRepository;

    @Autowired
    private BookshelfRepository bookshelfRepository;

    @Autowired
    private FriendsRepository friendsRepository;

    public List<FriendsUpdateDto> fetchFriendsUpdates(User user) {
        List<Ratings> friendsRatings = ratingsRepository.getFriendsRatings(user);
        List<FriendsReadingDto> friendsCurrentlyReading = bookshelfRepository.getFriendsBooksWithSpecifiedLabels(user, "Currently Reading");
        List<FriendsReadingDto> friendsRead = bookshelfRepository.getFriendsBooksWithSpecifiedLabels(user, "Read");
        List<FriendsReadingDto> friendsWantToRead = bookshelfRepository.getFriendsBooksWithSpecifiedLabels(user, "Want to Read");

        List<FriendsUpdateDto> allUpdates = new ArrayList<>();

        // Ratings
        for (Ratings rating : friendsRatings) {
            allUpdates.add(new FriendsUpdateDto(
                    "rating",
                    rating.getUser().getUsername(),
                    rating.getBook().getId(),
                    rating.getBook().getTitle(),
                    rating.getBook().getPhotoUrl(),
                    rating.getDateUpdated(),
                    rating.getStars(),
                    null
            ));
        }

        // Reviews
        for (Ratings rating : friendsRatings) {
            if (rating.getDescription() != null && !rating.getDescription().trim().isEmpty()) {
                allUpdates.add(new FriendsUpdateDto(
                        "review",
                        rating.getUser().getUsername(),
                        rating.getBook().getId(),
                        rating.getBook().getTitle(),
                        rating.getBook().getPhotoUrl(),
                        rating.getDateUpdated(),
                        0,
                        rating.getDescription()
                ));
            }
        }

        // Currently Reading
        for (FriendsReadingDto reading : friendsCurrentlyReading) {
            allUpdates.add(new FriendsUpdateDto(
                    "currentlyReading",
                    reading.getBookshelf().getUser().getUsername(),
                    reading.getBook().getId(),
                    reading.getBook().getTitle(),
                    reading.getBook().getPhotoUrl(),
                    reading.getShelfBook().getDateUpdated(),
                    0,
                    null
            ));
        }

        // Books Read
        for (FriendsReadingDto read : friendsRead) {
            allUpdates.add(new FriendsUpdateDto(
                    "read",
                    read.getBookshelf().getUser().getUsername(),
                    read.getBook().getId(),
                    read.getBook().getTitle(),
                    read.getBook().getPhotoUrl(),
                    read.getShelfBook().getDateUpdated(),
                    0,
                    null
            ));
        }

        // Want to Read
        for (FriendsReadingDto wantToRead : friendsWantToRead) {
            allUpdates.add(new FriendsUpdateDto(
                    "wantToRead",
                    wantToRead.getBookshelf().getUser().getUsername(),
                    wantToRead.getBook().getId(),
                    wantToRead.getBook().getTitle(),
                    wantToRead.getBook().getPhotoUrl(),
                    wantToRead.getShelfBook().getDateUpdated(),
                    0,
                    null
            ));
        }

        allUpdates.sort(Comparator.comparing(FriendsUpdateDto::getDateUpdated, Comparator.nullsLast(LocalDateTime::compareTo)).reversed());

        return allUpdates;
    }
}
