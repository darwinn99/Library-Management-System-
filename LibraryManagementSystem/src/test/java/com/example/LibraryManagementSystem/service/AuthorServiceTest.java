package com.example.LibraryManagementSystem.service;


import com.example.LibraryManagementSystem.domain.Author;
import com.example.LibraryManagementSystem.repo.AuthorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    private AuthorService authorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authorService = new AuthorService(authorRepository);
    }

    @Test
    void getAllAuthors_ShouldReturnAuthorsPage_WhenPageableProvided() {
        Pageable pageable = PageRequest.of(0, 10);
        Author author = new Author();
        author.setId(1L);
        author.setName("Test Author");
        Page<Author> authors = new PageImpl<>(Collections.singletonList(author));

        when(authorRepository.findAll(pageable)).thenReturn(authors);

        Page<Author> result = authorService.getAllAuthors(pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Author", result.getContent().get(0).getName());

        verify(authorRepository, times(1)).findAll(pageable);
    }

    @Test
    void getAuthorById_ShouldReturnAuthor_WhenAuthorExists() {
        Author author = new Author();
        author.setId(1L);
        author.setName("Test Author");

        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));

        Author result = authorService.getAuthorById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Author", result.getName());

        verify(authorRepository, times(1)).findById(1L);
    }

    @Test
    void getAuthorById_ShouldReturnNull_WhenAuthorDoesNotExist() {
        when(authorRepository.findById(1L)).thenReturn(Optional.empty());

        Author result = authorService.getAuthorById(1L);
        assertNull(result);

        verify(authorRepository, times(1)).findById(1L);
    }

    @Test
    void createAuthor_ShouldReturnSavedAuthor_WhenAuthorDetailsProvided() {
        Author author = new Author();
        author.setName("New Author");
        author.setBirthDate(new Date(1980, 1, 1));
        author.setNationality("Unknown");

        when(authorRepository.save(any(Author.class))).thenReturn(author);

        Author result = authorService.createAuthor(author);
        assertNotNull(result);
        assertEquals("New Author", result.getName());

        verify(authorRepository, times(1)).save(author);

    }

    @Test
    void updateAuthor_ShouldReturnUpdatedAuthor_WhenAuthorExists() {
        Author existingAuthor = new Author();
        existingAuthor.setId(1L);
        existingAuthor.setName("Existing Author");
        existingAuthor.setBirthDate(new Date(1970, 1, 1));
        existingAuthor.setNationality("Old Nationality");

        Author updatedAuthor = new Author();
        updatedAuthor.setName("Updated Author");
        updatedAuthor.setBirthDate(new Date(1980, 1, 1));
        updatedAuthor.setNationality("New Nationality");

        when(authorRepository.findById(1L)).thenReturn(Optional.of(existingAuthor));
        when(authorRepository.save(any(Author.class))).thenReturn(existingAuthor);

        Author result = authorService.updateAuthor(1L, updatedAuthor);
        assertNotNull(result);
        assertEquals("Updated Author", result.getName());
        assertEquals(new Date(1980, 1, 1), result.getBirthDate());
        assertEquals("New Nationality", result.getNationality());

        verify(authorRepository, times(1)).findById(1L);
        verify(authorRepository, times(1)).save(existingAuthor);
    }

    @Test
    void updateAuthor_ShouldReturnNull_WhenAuthorDoesNotExist() {
        Author updatedAuthor = new Author();
        updatedAuthor.setName("Updated Author");

        when(authorRepository.findById(1L)).thenReturn(Optional.empty());

        Author result = authorService.updateAuthor(1L, updatedAuthor);
        assertNull(result);

        verify(authorRepository, times(1)).findById(1L);
        verify(authorRepository, times(0)).save(any(Author.class));
    }

    @Test
    void deleteAuthor_ShouldDeleteAuthor_WhenAuthorExists() {
        doNothing().when(authorRepository).deleteById(1L);

        authorService.deleteAuthor(1L);

        verify(authorRepository, times(1)).deleteById(1L);
    }
}

