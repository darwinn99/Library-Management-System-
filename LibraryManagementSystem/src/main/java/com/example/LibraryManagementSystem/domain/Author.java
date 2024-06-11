package com.example.LibraryManagementSystem.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class Author {
    @Id
    @Column(name = "author_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Size(min = 1, max = 100, message = "Author name must be between 1 and 100 characters")
    @NotNull(message = "Author name cannot be null")
    private String name;

    @NotNull
    @Temporal(TemporalType.DATE)
    private Date birthDate;

    @NotNull
    @Size(min = 1, max = 100, message = "nationality size must be between 1 and 100 characters")
    private String nationality;
}
