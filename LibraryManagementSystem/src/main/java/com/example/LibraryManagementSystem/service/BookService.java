package com.example.LibraryManagementSystem.service;

import com.example.LibraryManagementSystem.domain.Author;
import com.example.LibraryManagementSystem.domain.Book;
import com.example.LibraryManagementSystem.repo.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {


    private BookRepository bookRepository;
    private AuthorService authorService;

    public BookService(BookRepository bookRepository, AuthorService authorService) {
        this.bookRepository = bookRepository;
        this.authorService = authorService;
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }


    public Book getBookById(Long id) {
        Optional<Book> optionalBook = bookRepository.findById(id);
        return optionalBook.orElse(null);
    }


    public Book createBook(Book book) throws RuntimeException {
        Author author = authorService.getAuthorById(book.getAuthor().getId());

        if (author == null) {
            throw new RuntimeException("Author not found");
        }

        book.setAuthor(author);
        return bookRepository.save(book);
    }

    public Book updateBook(Long id, Book bookDetails) throws RuntimeException {
         Author author = authorService.getAuthorById(bookDetails.getAuthor().getId());

        if (author == null) {
            throw new RuntimeException("Author not found");
        }

        Optional<Book> optionalBook = bookRepository.findById(id);
        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            book.setTitle(bookDetails.getTitle());
            book.setAuthor(author); // Set the existing author
            book.setIsbn(bookDetails.getIsbn());
            book.setPublicationDate(bookDetails.getPublicationDate());
            book.setGenre(bookDetails.getGenre());
            book.setAvailable(bookDetails.getAvailable());
            return bookRepository.save(book);
        }
        return null;
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    public List<Book> searchBooksByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }

    public List<Book> searchBooksByAuthor(String author) {
        return bookRepository.findByAuthorNameContainingIgnoreCase(author);
    }

    public List<Book> searchBooksByIsbn(String isbn) {
        return bookRepository.findByIsbnContaining(isbn);
    }
}