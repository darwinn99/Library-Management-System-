package com.example.LibraryManagementSystem.service;

import com.example.LibraryManagementSystem.domain.Customer;
import com.example.LibraryManagementSystem.repo.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        customerService = new CustomerService(customerRepository, passwordEncoder);
    }

    @Test
    void getAllCustomers_ShouldReturnCustomersPage_WhenPageableProvided() {
        Pageable pageable = PageRequest.of(0, 10);
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("Test Customer");
        Page<Customer> customers = new PageImpl<>(Collections.singletonList(customer));

        when(customerRepository.findAll(pageable)).thenReturn(customers);

        Page<Customer> result = customerService.getAllCustomers(pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Customer", result.getContent().get(0).getName());

        verify(customerRepository, times(1)).findAll(pageable);
    }

    @Test
    void getCustomerById_ShouldReturnCustomer_WhenCustomerExists() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("Test Customer");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        Customer result = customerService.getCustomerById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Customer", result.getName());

        verify(customerRepository, times(1)).findById(1L);
    }

    @Test
    void getCustomerById_ShouldReturnNull_WhenCustomerDoesNotExist() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        Customer result = customerService.getCustomerById(1L);
        assertNull(result);

        verify(customerRepository, times(1)).findById(1L);
    }

    @Test
    void createCustomer_ShouldReturnSavedCustomer_WhenCustomerDetailsProvided() {
        Customer customer = new Customer();
        customer.setName("New Customer");
        customer.setPassword("password");

        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        Customer result = customerService.createCustomer(customer);
        assertNotNull(result);
        assertEquals("New Customer", result.getName());
        assertEquals("encodedPassword", result.getPassword());

        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository, times(1)).save(customerCaptor.capture());
        Customer capturedCustomer = customerCaptor.getValue();
        assertEquals("New Customer", capturedCustomer.getName());
        assertEquals("encodedPassword", capturedCustomer.getPassword());
    }

    @Test
    void updateCustomer_ShouldReturnUpdatedCustomer_WhenCustomerExists() {
        Customer existingCustomer = new Customer();
        existingCustomer.setId(1L);
        existingCustomer.setName("Existing Customer");

        Customer updatedCustomer = new Customer();
        updatedCustomer.setName("Updated Customer");
        updatedCustomer.setPassword("newPassword");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(existingCustomer));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(customerRepository.save(any(Customer.class))).thenReturn(existingCustomer);

        Customer result = customerService.updateCustomer(1L, updatedCustomer);
        assertNotNull(result);
        assertEquals("Updated Customer", result.getName());
        assertEquals("encodedNewPassword", result.getPassword());

        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository, times(1)).save(customerCaptor.capture());
        Customer capturedCustomer = customerCaptor.getValue();
        assertEquals("Updated Customer", capturedCustomer.getName());
        assertEquals("encodedNewPassword", capturedCustomer.getPassword());
    }

    @Test
    void updateCustomer_ShouldReturnNull_WhenCustomerDoesNotExist() {
        Customer updatedCustomer = new Customer();
        updatedCustomer.setName("Updated Customer");
        updatedCustomer.setPassword("newPassword");

        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        Customer result = customerService.updateCustomer(1L, updatedCustomer);
        assertNull(result);

        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void deleteCustomer_ShouldDeleteCustomer_WhenCustomerExists() {
        Long customerId = 1L;

        customerService.deleteCustomer(customerId);

        verify(customerRepository, times(1)).deleteById(customerId);
    }
}
