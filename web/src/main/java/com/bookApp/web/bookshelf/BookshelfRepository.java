package com.bookApp.web.bookshelf;

import com.bookApp.web.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookshelfRepository extends JpaRepository<Bookshelf, Long> {

}
