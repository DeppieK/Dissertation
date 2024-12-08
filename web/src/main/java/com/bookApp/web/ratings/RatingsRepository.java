package com.bookApp.web.ratings;

import com.bookApp.web.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RatingsRepository extends JpaRepository<Ratings, Long> {

    List<Ratings> findByBookId(Long bookId);

    Ratings findById(long id);

    Ratings findByBookIdAndUserId(Long bookId, Long userId);

    @Query("SELECT r FROM Ratings r JOIN Friends f " +
            "ON (r.user = f.receiver OR r.user = f.sender) " +
            "WHERE r.dateUpdated >= :thresholdDate " +
            "AND f.status = 'ACCEPTED' " +
            "AND (f.receiver = :currentUser OR f.sender = :currentUser) " +
            "AND r.user <> :currentUser")
    List<Ratings> getFriendsRatingsInASpecificTimestamp(@Param("currentUser") User currentUser, @Param("thresholdDate") LocalDateTime thresholdDate);

}
