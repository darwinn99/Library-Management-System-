package com.example.LibraryManagementSystem.service;

import com.example.LibraryManagementSystem.domain.Author;
import com.example.LibraryManagementSystem.repo.AuthorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorService {

    private AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }

    public Author getAuthorById(Long id) {
        return authorRepository.findById(id).orElse(null);
    }

    public Author createAuthor(Author author) {
        return authorRepository.save(author);
    }

    public Author updateAuthor(Long id, Author authorDetails) {
        Author author = authorRepository.findById(id).orElse(null);
        if (author != null) {
            author.setName(authorDetails.getName());
            author.setBirthDate(authorDetails.getBirthDate());
            author.setNationality(authorDetails.getNationality());
            return authorRepository.save(author);
        }
        return null;
    }

    public void deleteAuthor(Long id) {
        authorRepository.deleteById(id);
    }
}