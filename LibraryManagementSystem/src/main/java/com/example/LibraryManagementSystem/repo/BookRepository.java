package com.example.LibraryManagementSystem.repo;

import com.example.LibraryManagementSystem.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByTitleContainingIgnoreCase(String title);
    List<Book> findByAuthorNameContainingIgnoreCase(String author);
    List<Book> findByIsbnContaining(String isbn);
}