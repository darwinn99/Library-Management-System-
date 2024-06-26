package com.example.LibraryManagementSystem.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Entity
@Data
public class Book {
    @Id
    @Column(name = "book_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Book title cannot be null")
    @Size(min = 1, max = 200, message = "Book title must be between 1 and 200 characters")
    private String title;

    @NotNull(message = "Book Author cannot be null")
    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;

    @NotNull
    @Size(min = 1, max = 13, message = "Book ISBN must be between 1 and 13 characters")
    private String isbn;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BorrowingRecord> borrowingRecords;

    @Temporal(TemporalType.DATE)
    private Date publicationDate;


    @Size(min = 1, max = 50, message = "Book Genre must be between 1 and 50 characters")
    private String genre;

    private Boolean available;


}