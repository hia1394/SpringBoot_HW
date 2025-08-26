package com.rookies4.myspringbootlab.repository;

import com.rookies4.myspringbootlab.entity.BookDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookDetailRepository extends JpaRepository<BookDetail, Long> {
    Optional<BookDetail> findByBookId(Long bookId);
    List<BookDetail> findByPublisher(String publisher);
}