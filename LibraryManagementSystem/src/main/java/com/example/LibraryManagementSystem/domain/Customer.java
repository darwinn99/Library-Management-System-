package com.example.LibraryManagementSystem.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Entity
@Data
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 1, max = 100)
    private String name;

    @NotNull
    @Email
    @Size(max = 100)
    private String email;

    @NotNull
    @Size(max = 200)
    private String address;

    @NotNull
    @Size(min = 10, max = 15)
    private String phoneNumber;

    @NotNull
    private String password;


}