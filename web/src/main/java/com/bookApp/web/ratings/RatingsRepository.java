package com.bookApp.web.ratings;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingsRepository extends JpaRepository<Ratings, Long> {

    List<Ratings> findByBookId(Long bookId);
}
