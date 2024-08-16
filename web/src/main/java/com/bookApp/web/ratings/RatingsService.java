package com.bookApp.web.ratings;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RatingsService {
    private final RatingsRepository ratingsRepository;

    public RatingsService(RatingsRepository ratingsRepository) {
        this.ratingsRepository = ratingsRepository;
    }

    public List<Ratings> findByBookId(Long bookId) {
        return ratingsRepository.findByBookId(bookId);
    }

    public void save(Ratings ratings) {
        ratingsRepository.save(ratings);
    }

}
