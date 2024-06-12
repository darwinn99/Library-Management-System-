package com.example.LibraryManagementSystem.controller;

import com.example.LibraryManagementSystem.domain.Author;
import com.example.LibraryManagementSystem.service.AuthorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/authors")
public class AuthorController {

    private AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping()
    public ResponseEntity<PagedModel<EntityModel<Author>>> getAllAuthors(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 5);
        Page<Author> authorsPage = authorService.getAllAuthors(pageable);

        List<EntityModel<Author>> authors = authorsPage.getContent().stream()
                .map(author -> EntityModel.of(author,
                        linkTo(methodOn(AuthorController.class).getAuthorById(author.getId())).withSelfRel(),
                        linkTo(methodOn(AuthorController.class).getAllAuthors(page)).withRel("authors")))
                .collect(Collectors.toList());

        PagedModel<EntityModel<Author>> pagedModel = PagedModel.of(authors,
                new PagedModel.PageMetadata(authorsPage.getSize(), authorsPage.getNumber(), authorsPage.getTotalElements()));

        pagedModel.add(linkTo(methodOn(AuthorController.class).getAllAuthors(page)).withSelfRel());

        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAuthorById(@PathVariable Long id) {
        Author author = authorService.getAuthorById(id);
        if (author != null) {
            EntityModel<Author> resource = EntityModel.of(author,
                    linkTo(methodOn(AuthorController.class).getAuthorById(id)).withSelfRel(),
                    linkTo(methodOn(AuthorController.class).getAllAuthors(0)).withRel("authors"));
            return ResponseEntity.ok(resource);
        } else {
            return new ResponseEntity<>("There is no Author with this ID", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<EntityModel<Author>> createAuthor(@Valid @RequestBody Author author) {
        Author createdAuthor = authorService.createAuthor(author);
        EntityModel<Author> resource = EntityModel.of(createdAuthor,
                linkTo(methodOn(AuthorController.class).getAuthorById(createdAuthor.getId())).withSelfRel(),
                linkTo(methodOn(AuthorController.class).getAllAuthors(0)).withRel("authors"));
        return new ResponseEntity<>(resource, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Author>> updateAuthor(@PathVariable Long id, @Valid @RequestBody Author authorDetails) {
        Author updatedAuthor = authorService.updateAuthor(id, authorDetails);
        if (updatedAuthor != null) {
            EntityModel<Author> resource = EntityModel.of(updatedAuthor,
                    linkTo(methodOn(AuthorController.class).getAuthorById(updatedAuthor.getId())).withSelfRel(),
                    linkTo(methodOn(AuthorController.class).getAllAuthors(0)).withRel("authors"));
            return ResponseEntity.ok(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        authorService.deleteAuthor(id);
        return ResponseEntity.noContent().build();
    }
}
