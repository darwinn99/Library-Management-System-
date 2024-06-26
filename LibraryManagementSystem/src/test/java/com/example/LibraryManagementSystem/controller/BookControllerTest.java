package com.example.LibraryManagementSystem.controller;

import com.example.LibraryManagementSystem.domain.Author;
import com.example.LibraryManagementSystem.domain.Book;
import com.example.LibraryManagementSystem.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class BookControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BookService bookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        BookController bookController = new BookController(bookService);

        mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();

    }

    @Test
    void getAllBooks_ShouldReturnBooksPage() throws Exception {
        Pageable pageable = PageRequest.of(0, 5);
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        Page<Book> booksPage = new PageImpl<>(Collections.singletonList(book));

        when(bookService.getAllBooks(pageable)).thenReturn(booksPage);

        mockMvc.perform(get("/books")
                        .param("page", "0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(book.getId()))
                .andExpect(jsonPath("$.content[0].title").value(book.getTitle()));

        verify(bookService, times(1)).getAllBooks(pageable);
    }

    @Test
    void getBookById_ShouldReturnBook_WhenBookExists() throws Exception {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");

        when(bookService.getBookById(1L)).thenReturn(book);

        mockMvc.perform(get("/books/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(book.getId()))
                .andExpect(jsonPath("$.title").value(book.getTitle()));

        verify(bookService, times(1)).getBookById(1L);
    }

    @Test
    void getBookById_ShouldReturnNotFound_WhenBookDoesNotExist() throws Exception {
        when(bookService.getBookById(1L)).thenReturn(null);

        mockMvc.perform(get("/books/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("There is no Book with this ID"));

        verify(bookService, times(1)).getBookById(1L);
    }

    @Test
    void createBook_ShouldReturnCreatedBook() throws Exception {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("New Book");
        Author author = new Author();

        book.setAuthor(author);
        book.setIsbn("1234567890");

        when(bookService.createBook(any(Book.class))).thenReturn(book);

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"New Book\", \"author\": {\"id\": 1, \"name\": \"Author Name\"}, \"isbn\": \"1234567890\", \"publicationDate\": \"2023-06-22\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(book.getId()))
                .andExpect(jsonPath("$.title").value(book.getTitle()));

        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        verify(bookService, times(1)).createBook(bookCaptor.capture());
        assertEquals("New Book", bookCaptor.getValue().getTitle());
    }

    @Test
    void updateBook_ShouldReturnUpdatedBook_WhenBookExists() throws Exception {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Updated Book");
        book.setAuthor(new Author()); // Provide necessary fields
        book.setIsbn("0987654321"); // Provide necessary fields

        when(bookService.updateBook(anyLong(), any(Book.class))).thenReturn(book);

        mockMvc.perform(put("/books/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Updated Book\", \"author\": {\"id\": 1, \"name\": \"Updated Author\"}, \"birthDate\": \"2023-01-01\", \"isbn\": \"0987654321\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(book.getId()))
                .andExpect(jsonPath("$.title").value(book.getTitle()));

        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        verify(bookService, times(1)).updateBook(anyLong(), bookCaptor.capture());
        assertEquals("Updated Book", bookCaptor.getValue().getTitle());
    }

    @Test
    void updateBook_ShouldReturnNotFound_WhenBookDoesNotExist() throws Exception {
        when(bookService.updateBook(anyLong(), any(Book.class))).thenReturn(null);

        mockMvc.perform(put("/books/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Updated Book\", \"author\": {\"id\": 1, \"name\": \"Updated Author\"}, \"birthDate\": \"2023-01-01\", \"isbn\": \"0987654321\"}"))
                .andExpect(status().isNotFound());

        verify(bookService, times(1)).updateBook(anyLong(), any(Book.class));
    }

    @Test
    void deleteBook_ShouldDeleteBook_WhenBookExists() throws Exception {
        doNothing().when(bookService).deleteBook(anyLong());

        mockMvc.perform(delete("/books/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(bookService, times(1)).deleteBook(1L);
    }

    @Test
    void deleteBook_ShouldReturnConflict_WhenBookIsBorrowed() throws Exception {
        doThrow(new RuntimeException("This book is borrowed and can't be deleted now.")).when(bookService).deleteBook(anyLong());

        mockMvc.perform(delete("/books/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(content().string("This book is borrowed and can't be deleted now."));

        verify(bookService, times(1)).deleteBook(1L);
    }

    @Test
    void searchBooks_ShouldReturnBooks_WhenSearchByTitle() throws Exception {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");

        when(bookService.searchBooksByTitle("Test Book")).thenReturn(Collections.singletonList(book));

        mockMvc.perform(get("/books/search")
                        .param("title", "Test Book")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(book.getId()))
                .andExpect(jsonPath("$[0].title").value(book.getTitle()));

        verify(bookService, times(1)).searchBooksByTitle("Test Book");
    }

    @Test
    void searchBooks_ShouldReturnBooks_WhenSearchByAuthor() throws Exception {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");

        when(bookService.searchBooksByAuthor("Author Name")).thenReturn(Collections.singletonList(book));

        mockMvc.perform(get("/books/search")
                        .param("author", "Author Name")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(book.getId()))
                .andExpect(jsonPath("$[0].title").value(book.getTitle()));

        verify(bookService, times(1)).searchBooksByAuthor("Author Name");
    }

    @Test
    void searchBooks_ShouldReturnBooks_WhenSearchByIsbn() throws Exception {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");

        when(bookService.searchBooksByIsbn("1234567890")).thenReturn(Collections.singletonList(book));

        mockMvc.perform(get("/books/search")
                        .param("isbn", "1234567890")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(book.getId()))
                .andExpect(jsonPath("$[0].title").value(book.getTitle()));

        verify(bookService, times(1)).searchBooksByIsbn("1234567890");
    }

    @Test
    void searchBooks_ShouldReturnBadRequest_WhenMultipleParamsProvided() throws Exception {
        mockMvc.perform(get("/books/search")
                        .param("title", "Test Book")
                        .param("author", "Author Name")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Please provide exactly one query parameter: title, author, or isbn"));

        verify(bookService, never()).searchBooksByTitle(anyString());
        verify(bookService, never()).searchBooksByAuthor(anyString());
        verify(bookService, never()).searchBooksByIsbn(anyString());
    }
}
