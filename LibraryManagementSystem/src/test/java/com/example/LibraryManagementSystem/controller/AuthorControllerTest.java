package com.example.LibraryManagementSystem.controller;

import com.example.LibraryManagementSystem.domain.Author;
import com.example.LibraryManagementSystem.service.AuthorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthorControllerTest {

    @Mock
    private AuthorService authorService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        AuthorController authorController = new AuthorController(authorService);
        mockMvc = MockMvcBuilders.standaloneSetup(authorController).build();
    }

    @Test
    void getAllAuthors_ShouldReturnAuthorsPage() throws Exception {
        Pageable pageable = PageRequest.of(0, 5);
        Author author = new Author();
        author.setId(1L);
        author.setName("Test Author");
        Page<Author> authorsPage = new PageImpl<>(Collections.singletonList(author));

        when(authorService.getAllAuthors(pageable)).thenReturn(authorsPage);

        mockMvc.perform(get("/authors")
                        .param("page", "0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(author.getId()))
                .andExpect(jsonPath("$.content[0].name").value(author.getName()));

        verify(authorService, times(1)).getAllAuthors(pageable);
    }

    @Test
    void getAuthorById_ShouldReturnAuthor_WhenAuthorExists() throws Exception {
        Author author = new Author();
        author.setId(1L);
        author.setName("Test Author");

        when(authorService.getAuthorById(1L)).thenReturn(author);

        mockMvc.perform(get("/authors/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(author.getId()))
                .andExpect(jsonPath("$.name").value(author.getName()));

        verify(authorService, times(1)).getAuthorById(1L);
    }

    @Test
    void getAuthorById_ShouldReturnNotFound_WhenAuthorDoesNotExist() throws Exception {
        when(authorService.getAuthorById(1L)).thenReturn(null);

        mockMvc.perform(get("/authors/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("There is no Author with this ID"));

        verify(authorService, times(1)).getAuthorById(1L);
    }

    @Test
    void createAuthor_ShouldReturnCreatedAuthor() throws Exception {
        Author author = new Author();
        author.setId(1L);
        author.setName("New Author");
        author.setBirthDate(new Date(2023- 1 - 1)); // provide a valid date
        author.setNationality("Some Nationality");

        when(authorService.createAuthor(any(Author.class))).thenReturn(author);

        mockMvc.perform(post("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"New Author\", \"birthDate\": \"2023-01-01\", \"nationality\": \"Some Nationality\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(author.getId()))
                .andExpect(jsonPath("$.name").value(author.getName()))
                .andExpect(jsonPath("$.birthDate").exists())
                .andExpect(jsonPath("$.nationality").value(author.getNationality()));

        ArgumentCaptor<Author> authorCaptor = ArgumentCaptor.forClass(Author.class);
        verify(authorService, times(1)).createAuthor(authorCaptor.capture());
        assertEquals("New Author", authorCaptor.getValue().getName());
    }

    @Test
    void updateAuthor_ShouldReturnUpdatedAuthor_WhenAuthorExists() throws Exception {
        Author author = new Author();
        author.setId(1L);
        author.setName("Updated Author");
        author.setBirthDate(new Date(2023- 1 - 1)); // provide a valid date
        author.setNationality("Some Nationality");

        when(authorService.updateAuthor(anyLong(), any(Author.class))).thenReturn(author);

        mockMvc.perform(put("/authors/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Updated Author\" , \"birthDate\": \"2023-01-01\", \"nationality\": \"Some Nationality\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(author.getId()))
                .andExpect(jsonPath("$.name").value(author.getName()));

        ArgumentCaptor<Author> authorCaptor = ArgumentCaptor.forClass(Author.class);
        verify(authorService, times(1)).updateAuthor(anyLong(), authorCaptor.capture());
        assertEquals("Updated Author", authorCaptor.getValue().getName());
    }

    @Test
    void updateAuthor_ShouldReturnNotFound_WhenAuthorDoesNotExist() throws Exception {
        when(authorService.updateAuthor(anyLong(), any(Author.class))).thenReturn(null);

        mockMvc.perform(put("/authors/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Updated Author\", \"birthDate\": \"2023-01-01\", \"nationality\": \"Updated Nationality\"}"))
                .andExpect(status().isNotFound());

        verify(authorService, times(1)).updateAuthor(anyLong(), any(Author.class));
    }


    @Test
    void deleteAuthor_ShouldDeleteAuthor() throws Exception {
        mockMvc.perform(delete("/authors/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(authorService, times(1)).deleteAuthor(1L);
    }
}
