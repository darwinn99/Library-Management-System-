package com.example.LibraryManagementSystem.repo;

import com.example.LibraryManagementSystem.domain.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
}