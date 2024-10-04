package com.bookApp.web.shelf_book;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShelfBookRepository extends JpaRepository<ShelfBook, Integer> {

    List<ShelfBook> findByShelfId(Long shelfId);

    List<ShelfBook> findAllByShelfId(Long shelfId);

    List<ShelfBook> findAll();

    @Query("SELECT COUNT(sb) FROM ShelfBook sb WHERE sb.shelfId = :shelfId")
    Long countBooksByShelfId(@Param("shelfId") Long shelfId);
}
