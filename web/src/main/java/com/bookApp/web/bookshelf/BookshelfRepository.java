package com.bookApp.web.bookshelf;

import com.bookApp.web.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookshelfRepository extends JpaRepository<Bookshelf, Long> {
    List<Bookshelf> findByUser(User user);

    long countByUserAndLabel(User user, String label);

    @Query("SELECT b FROM Bookshelf b WHERE b.user = :user AND b.label NOT IN ('currently_reading', 'to_read', 'want_to_read')")
    List<Bookshelf> findBookshelvesWithoutSpecifiedLabels(User user);

    List<Bookshelf> findByUserAndLabelIn(User user, List<String> labels);

}
