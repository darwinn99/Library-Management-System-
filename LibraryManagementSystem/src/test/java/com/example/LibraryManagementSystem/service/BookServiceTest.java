package com.example.LibraryManagementSystem.service;

import com.example.LibraryManagementSystem.domain.Author;
import com.example.LibraryManagementSystem.domain.Book;
import com.example.LibraryManagementSystem.repo.BookRepository;
import com.example.LibraryManagementSystem.repo.BorrowingRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorService authorService;

    @Mock
    private BorrowingRecordRepository borrowingRecordRepository;

    private BookService bookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bookService = new BookService(bookRepository, authorService, borrowingRecordRepository);
    }

    @Test
    void getAllBooks_ShouldReturnBooksPage_WhenPageableProvided() {
        Pageable pageable = PageRequest.of(0, 10);
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        Page<Book> books = new PageImpl<>(Collections.singletonList(book));

        when(bookRepository.findAll(pageable)).thenReturn(books);

        Page<Book> result = bookService.getAllBooks(pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Book", result.getContent().get(0).getTitle());

        verify(bookRepository, times(1)).findAll(pageable);
    }

    @Test
    void getBookById_ShouldReturnBook_WhenBookExists() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Book result = bookService.getBookById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Book", result.getTitle());

        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void getBookById_ShouldReturnNull_WhenBookDoesNotExist() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        Book result = bookService.getBookById(1L);
        assertNull(result);

        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void createBook_ShouldReturnSavedBook_WhenAuthorExists() {
        Author author = new Author();
        author.setId(1L);
        author.setName("Test Author");

        Book book = new Book();
        book.setTitle("New Book");
        book.setAuthor(author);

        when(authorService.getAuthorById(1L)).thenReturn(author);
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        Book result = bookService.createBook(book);
        assertNotNull(result);
        assertEquals("New Book", result.getTitle());
        assertEquals(author, result.getAuthor());

        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository, times(1)).save(bookCaptor.capture());
        Book capturedBook = bookCaptor.getValue();
        assertEquals("New Book", capturedBook.getTitle());
        assertEquals(author, capturedBook.getAuthor());
    }

    @Test
    void createBook_ShouldThrowException_WhenAuthorDoesNotExist() {
        Book book = new Book();
        book.setTitle("New Book");
        Author author = new Author();
        author.setId(1L);
        book.setAuthor(author);

        when(authorService.getAuthorById(1L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> bookService.createBook(book));

        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void updateBook_ShouldReturnUpdatedBook_WhenBookAndAuthorExist() {
        Author author = new Author();
        author.setId(1L);
        author.setName("Test Author");

        Book existingBook = new Book();
        existingBook.setId(1L);
        existingBook.setTitle("Existing Book");

        Book updatedBook = new Book();
        updatedBook.setTitle("Updated Book");
        updatedBook.setAuthor(author);

        when(authorService.getAuthorById(1L)).thenReturn(author);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(any(Book.class))).thenReturn(existingBook);

        Book result = bookService.updateBook(1L, updatedBook);
        assertNotNull(result);
        assertEquals("Updated Book", result.getTitle());
        assertEquals(author, result.getAuthor());

        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository, times(1)).save(bookCaptor.capture());
        Book capturedBook = bookCaptor.getValue();
        assertEquals("Updated Book", capturedBook.getTitle());
        assertEquals(author, capturedBook.getAuthor());
    }

    @Test
    void updateBook_ShouldReturnNull_WhenBookDoesNotExist() {
        Book updatedBook = new Book();
        updatedBook.setTitle("Updated Book");
        Author author = new Author();
        author.setId(1L);
        updatedBook.setAuthor(author);

        when(authorService.getAuthorById(1L)).thenReturn(author);
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        Book result = bookService.updateBook(1L, updatedBook);
        assertNull(result);

        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void updateBook_ShouldThrowException_WhenAuthorDoesNotExist() {
        Book updatedBook = new Book();
        updatedBook.setTitle("Updated Book");
        Author author = new Author();
        author.setId(1L);
        updatedBook.setAuthor(author);

        when(authorService.getAuthorById(1L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> bookService.updateBook(1L, updatedBook));

        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void deleteBook_ShouldThrowException_WhenBookIsBorrowed() {
        Long bookId = 1L;
        when(borrowingRecordRepository.existsByBookIdAndReturnDateIsNull(bookId)).thenReturn(true);

        assertThrows(RuntimeException.class, () -> bookService.deleteBook(bookId));
        verify(bookRepository, never()).deleteById(bookId);
    }

    @Test
    void deleteBook_ShouldDeleteBook_WhenBookIsNotBorrowed() {
        Long bookId = 1L;
        when(borrowingRecordRepository.existsByBookIdAndReturnDateIsNull(bookId)).thenReturn(false);

        bookService.deleteBook(bookId);
        verify(bookRepository, times(1)).deleteById(bookId);
    }

    @Test
    void searchBooksByTitle_ShouldReturnBooks_WhenTitleMatches() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");

        when(bookRepository.findByTitleContainingIgnoreCase("Test")).thenReturn(Collections.singletonList(book));

        List<Book> result = bookService.searchBooksByTitle("Test");
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Book", result.get(0).getTitle());

        verify(bookRepository, times(1)).findByTitleContainingIgnoreCase("Test");
    }

    @Test
    void searchBooksByAuthor_ShouldReturnBooks_WhenAuthorMatches() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");

        when(bookRepository.findByAuthorNameContainingIgnoreCase("Author")).thenReturn(Collections.singletonList(book));

        List<Book> result = bookService.searchBooksByAuthor("Author");
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Book", result.get(0).getTitle());

        verify(bookRepository, times(1)).findByAuthorNameContainingIgnoreCase("Author");
    }

    @Test
    void searchBooksByIsbn_ShouldReturnBooks_WhenIsbnMatches() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");

        when(bookRepository.findByIsbnContaining("123456789")).thenReturn(Collections.singletonList(book));

        List<Book> result = bookService.searchBooksByIsbn("123456789");
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Book", result.get(0).getTitle());

        verify(bookRepository, times(1)).findByIsbnContaining("123456789");
    }
}
