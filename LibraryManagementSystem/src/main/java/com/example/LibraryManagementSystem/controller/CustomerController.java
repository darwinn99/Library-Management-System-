package com.example.LibraryManagementSystem.controller;

import com.example.LibraryManagementSystem.domain.Customer;
import com.example.LibraryManagementSystem.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Customer>>> getAllCustomers(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 5);
        Page<Customer> customersPage = customerService.getAllCustomers(pageable);

        List<EntityModel<Customer>> customers = customersPage.getContent().stream()
                .map(customer -> EntityModel.of(customer,
                        linkTo(methodOn(CustomerController.class).getCustomerById(customer.getId())).withSelfRel(),
                        linkTo(methodOn(CustomerController.class).getAllCustomers(page)).withRel("customers")))
                .collect(Collectors.toList());

        PagedModel<EntityModel<Customer>> pagedModel = PagedModel.of(customers,
                new PagedModel.PageMetadata(customersPage.getSize(), customersPage.getNumber(), customersPage.getTotalElements()));

        pagedModel.add(linkTo(methodOn(CustomerController.class).getAllCustomers(page)).withSelfRel());

        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomerById(@PathVariable Long id) {
        Customer customer = customerService.getCustomerById(id);
        if (customer != null) {
            EntityModel<Customer> customerModel = EntityModel.of(customer,
                    linkTo(methodOn(CustomerController.class).getCustomerById(id)).withSelfRel(),
                    linkTo(methodOn(CustomerController.class).getAllCustomers(0)).withRel("customers"));
            return ResponseEntity.ok(customerModel);
        } else {
            return new ResponseEntity<>("There is no Customer with this ID", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<?> createCustomer(@Valid @RequestBody Customer customer) {
        Customer createdCustomer = customerService.createCustomer(customer);
        EntityModel<Customer> customerModel = EntityModel.of(createdCustomer,
                linkTo(methodOn(CustomerController.class).getCustomerById(createdCustomer.getId())).withSelfRel(),
                linkTo(methodOn(CustomerController.class).getAllCustomers(0)).withRel("customers"));
        return new ResponseEntity<>(customerModel, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable Long id, @Valid @RequestBody Customer customerDetails) {
        Customer updatedCustomer = customerService.updateCustomer(id, customerDetails);
        if (updatedCustomer != null) {
            EntityModel<Customer> customerModel = EntityModel.of(updatedCustomer,
                    linkTo(methodOn(CustomerController.class).getCustomerById(updatedCustomer.getId())).withSelfRel(),
                    linkTo(methodOn(CustomerController.class).getAllCustomers(0)).withRel("customers"));
            return ResponseEntity.ok(customerModel);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}
