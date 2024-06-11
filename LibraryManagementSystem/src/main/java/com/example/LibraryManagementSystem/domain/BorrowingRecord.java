package com.example.LibraryManagementSystem.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class BorrowingRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @NotNull
    @Temporal(TemporalType.DATE)
    private Date borrowDate;

    @Temporal(TemporalType.DATE)
    private Date returnDate;

    // Getters and setters
    // ...
}