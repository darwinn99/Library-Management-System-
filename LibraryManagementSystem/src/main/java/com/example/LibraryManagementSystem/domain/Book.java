package com.example.LibraryManagementSystem.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class Book {
    @Id
    @Column(name = "book_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 1, max = 200)
    private String title;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;

    @NotNull
    @Size(min = 10, max = 13)
    private String isbn;

    @Temporal(TemporalType.DATE)
    private Date publicationDate;

    @Size(max = 50)
    private String genre;

    @NotNull
    private Boolean available;


}