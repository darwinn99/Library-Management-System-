package com.example.LibraryManagementSystem.controller;

import com.example.LibraryManagementSystem.domain.Book;
import com.example.LibraryManagementSystem.service.BookService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/books")

public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Book>>> getAllBooks(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 5);
        Page<Book> booksPage = bookService.getAllBooks(pageable);

        List<EntityModel<Book>> books = booksPage.getContent().stream()
                .map(book -> EntityModel.of(book,
                        linkTo(methodOn(BookController.class).getBookById(book.getId())).withSelfRel(),
                        linkTo(methodOn(BookController.class).getAllBooks(page)).withRel("books")))
                .collect(Collectors.toList());

        PagedModel<EntityModel<Book>> pagedModel = PagedModel.of(books,
                new PagedModel.PageMetadata(booksPage.getSize(), booksPage.getNumber(), booksPage.getTotalElements()));

        pagedModel.add(linkTo(methodOn(BookController.class).getAllBooks(page)).withSelfRel());

        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBookById(@PathVariable Long id) {
        Book book = bookService.getBookById(id);
        if (book != null) {
            EntityModel<Book> bookModel = EntityModel.of(book,
                    linkTo(methodOn(BookController.class).getBookById(id)).withSelfRel(),
                    linkTo(methodOn(BookController.class).getAllBooks(0)).withRel("books"));
            return ResponseEntity.ok(bookModel);
        } else {
            return new ResponseEntity<>("There is no Book with this ID", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<?> createBook(@Valid @RequestBody Book book) {
        try {
            Book createdBook = bookService.createBook(book);
            EntityModel<Book> bookModel = EntityModel.of(createdBook,
                    linkTo(methodOn(BookController.class).getBookById(createdBook.getId())).withSelfRel(),
                    linkTo(methodOn(BookController.class).getAllBooks(0)).withRel("books"));
            return new ResponseEntity<>(bookModel, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBook(@PathVariable Long id, @Valid @RequestBody Book bookDetails) {
        try {
            Book updatedBook = bookService.updateBook(id, bookDetails);
            EntityModel<Book> bookModel = EntityModel.of(updatedBook,
                    linkTo(methodOn(BookController.class).getBookById(updatedBook.getId())).withSelfRel(),
                    linkTo(methodOn(BookController.class).getAllBooks(0)).withRel("books"));
            return ResponseEntity.ok(bookModel);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchBooks(@RequestParam(required = false) String title,
                                         @RequestParam(required = false) String author,
                                         @RequestParam(required = false) String isbn) {
        int count = 0;
        if (title != null) count++;
        if (author != null) count++;
        if (isbn != null) count++;

        if (count != 1) {
            return new ResponseEntity<>("Please provide exactly one query parameter: title, author, or isbn", HttpStatus.BAD_REQUEST);
        }

        List<Book> books;

        if (title != null) {
            books = bookService.searchBooksByTitle(title);
        } else if (author != null) {
            books = bookService.searchBooksByAuthor(author);
        } else {
            books = bookService.searchBooksByIsbn(isbn);
        }

        List<EntityModel<Book>> bookModels = books.stream()
                .map(book -> EntityModel.of(book,
                        linkTo(methodOn(BookController.class).getBookById(book.getId())).withSelfRel(),
                        linkTo(methodOn(BookController.class).getAllBooks(0)).withRel("books")))
                .collect(Collectors.toList());

        return new ResponseEntity<>(bookModels, HttpStatus.OK);
    }
}
