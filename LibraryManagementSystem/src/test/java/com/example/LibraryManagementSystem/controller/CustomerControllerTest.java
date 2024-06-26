package com.example.LibraryManagementSystem.controller;

import com.example.LibraryManagementSystem.domain.Customer;
import com.example.LibraryManagementSystem.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    private CustomerController customerController;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        customerController = new CustomerController(customerService);
        mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();
    }

    @Test
    void getAllCustomers_ShouldReturnCustomersPage() throws Exception {
        Pageable pageable = PageRequest.of(0, 5);
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("Test Customer");
        Page<Customer> customersPage = new PageImpl<>(Collections.singletonList(customer));

        when(customerService.getAllCustomers(pageable)).thenReturn(customersPage);

        mockMvc.perform(get("/customers")
                        .param("page", "0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(customer.getId()))
                .andExpect(jsonPath("$.content[0].name").value(customer.getName()));

        verify(customerService, times(1)).getAllCustomers(pageable);
    }

    @Test
    void getCustomerById_ShouldReturnCustomer_WhenCustomerExists() throws Exception {
        Long customerId = 1L;
        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setName("Test Customer");

        when(customerService.getCustomerById(customerId)).thenReturn(customer);

        mockMvc.perform(get("/customers/{id}", customerId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customer.getId()))
                .andExpect(jsonPath("$.name").value(customer.getName()));

        verify(customerService, times(1)).getCustomerById(customerId);
    }

    @Test
    void getCustomerById_ShouldReturnNotFound_WhenCustomerDoesNotExist() throws Exception {
        Long nonExistentCustomerId = 999L;
        when(customerService.getCustomerById(nonExistentCustomerId)).thenReturn(null);

        mockMvc.perform(get("/customers/{id}", nonExistentCustomerId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("There is no Customer with this ID"));

        verify(customerService, times(1)).getCustomerById(nonExistentCustomerId);
    }

    @Test
    void createCustomer_ShouldReturnCreatedCustomer() throws Exception {
        Customer newCustomer = new Customer();
        newCustomer.setId(1L);
        newCustomer.setName("New Customer");

        when(customerService.createCustomer(any(Customer.class))).thenReturn(newCustomer);

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"New Customer\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(newCustomer.getId()))
                .andExpect(jsonPath("$.name").value(newCustomer.getName()));

        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerService, times(1)).createCustomer(customerCaptor.capture());
        assertEquals("New Customer", customerCaptor.getValue().getName());
    }

    @Test
    void updateCustomer_ShouldReturnUpdatedCustomer_WhenCustomerExists() throws Exception {
        Long customerId = 1L;
        Customer existingCustomer = new Customer();
        existingCustomer.setId(customerId);
        existingCustomer.setName("Updated Customer");

        when(customerService.updateCustomer(eq(customerId), any(Customer.class))).thenReturn(existingCustomer);

        mockMvc.perform(put("/customers/{id}", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Updated Customer\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingCustomer.getId()))
                .andExpect(jsonPath("$.name").value(existingCustomer.getName()));

        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerService, times(1)).updateCustomer(eq(customerId), customerCaptor.capture());
        assertEquals("Updated Customer", customerCaptor.getValue().getName());
    }

    @Test
    void updateCustomer_ShouldReturnNotFound_WhenCustomerDoesNotExist() throws Exception {
        Long nonExistentCustomerId = 999L;
        when(customerService.updateCustomer(eq(nonExistentCustomerId), any(Customer.class))).thenReturn(null);

        mockMvc.perform(put("/customers/{id}", nonExistentCustomerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Updated Customer\"}"))
                .andExpect(status().isNotFound());

        verify(customerService, times(1)).updateCustomer(eq(nonExistentCustomerId), any(Customer.class));
    }

    @Test
    void deleteCustomer_ShouldDeleteCustomer() throws Exception {
        Long customerId = 1L;
        mockMvc.perform(delete("/customers/{id}", customerId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(customerService, times(1)).deleteCustomer(customerId);
    }
}
