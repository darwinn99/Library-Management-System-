package com.example.LibraryManagementSystem.service;

import com.example.LibraryManagementSystem.domain.Book;
import com.example.LibraryManagementSystem.domain.BorrowingRecord;
import com.example.LibraryManagementSystem.domain.Customer;
import com.example.LibraryManagementSystem.repo.BorrowingRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class BorrowingRecordServiceTest {

    @Mock
    private BorrowingRecordRepository borrowingRecordRepository;

    @Mock
    private BookService bookService;

    @Mock
    private CustomerService customerService;

    private BorrowingRecordService borrowingRecordService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        borrowingRecordService = new BorrowingRecordService(borrowingRecordRepository, customerService, bookService);
    }

    @Test
    void getAllBorrowingRecords_ShouldReturnRecordsPage_WhenPageableProvided() {
        Pageable pageable = PageRequest.of(0, 10);
        BorrowingRecord record = new BorrowingRecord();
        record.setId(1L);
        Page<BorrowingRecord> records = new PageImpl<>(Collections.singletonList(record));

        when(borrowingRecordRepository.findAll(pageable)).thenReturn(records);

        Page<BorrowingRecord> result = borrowingRecordService.getAllBorrowingRecords(pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1L, result.getContent().get(0).getId());

        verify(borrowingRecordRepository, times(1)).findAll(pageable);
    }

    @Test
    void getBorrowingRecordById_ShouldReturnRecord_WhenRecordExists() {
        BorrowingRecord record = new BorrowingRecord();
        record.setId(1L);

        when(borrowingRecordRepository.findById(1L)).thenReturn(Optional.of(record));

        BorrowingRecord result = borrowingRecordService.getBorrowingRecordById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());

        verify(borrowingRecordRepository, times(1)).findById(1L);
    }

    @Test
    void getBorrowingRecordById_ShouldReturnNull_WhenRecordDoesNotExist() {
        when(borrowingRecordRepository.findById(1L)).thenReturn(Optional.empty());

        BorrowingRecord result = borrowingRecordService.getBorrowingRecordById(1L);
        assertNull(result);

        verify(borrowingRecordRepository, times(1)).findById(1L);
    }

    @Test
    void createBorrowingRecord_ShouldReturnSavedRecord_WhenCustomerAndBookExist() {
        Customer customer = new Customer();
        customer.setId(1L);
        Book book = new Book();
        book.setId(1L);
        BorrowingRecord record = new BorrowingRecord();

        when(customerService.getCustomerById(1L)).thenReturn(customer);
        when(bookService.getBookById(1L)).thenReturn(book);
        when(borrowingRecordRepository.save(any(BorrowingRecord.class))).thenReturn(record);

        BorrowingRecord result = borrowingRecordService.createBorrowingRecord(1L, 1L, record);
        assertNotNull(result);
        assertEquals(customer, result.getCustomer());
        assertEquals(book, result.getBook());

        ArgumentCaptor<BorrowingRecord> recordCaptor = ArgumentCaptor.forClass(BorrowingRecord.class);
        verify(borrowingRecordRepository, times(1)).save(recordCaptor.capture());
        BorrowingRecord capturedRecord = recordCaptor.getValue();
        assertEquals(customer, capturedRecord.getCustomer());
        assertEquals(book, capturedRecord.getBook());
    }

    @Test
    void createBorrowingRecord_ShouldThrowException_WhenCustomerOrBookDoesNotExist() {
        BorrowingRecord record = new BorrowingRecord();

        when(customerService.getCustomerById(1L)).thenReturn(null);
        when(bookService.getBookById(1L)).thenReturn(new Book());

        assertThrows(RuntimeException.class, () -> borrowingRecordService.createBorrowingRecord(1L, 1L, record));

        when(customerService.getCustomerById(1L)).thenReturn(new Customer());
        when(bookService.getBookById(1L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> borrowingRecordService.createBorrowingRecord(1L, 1L, record));

        verify(borrowingRecordRepository, never()).save(any(BorrowingRecord.class));
    }

    @Test
    void updateBorrowingRecord_ShouldReturnUpdatedRecord_WhenCustomerBookAndRecordExist() {
        Customer customer = new Customer();
        customer.setId(1L);
        Book book = new Book();
        book.setId(1L);
        BorrowingRecord existingRecord = new BorrowingRecord();
        existingRecord.setId(1L);
        BorrowingRecord updatedDetails = new BorrowingRecord();
        updatedDetails.setBorrowDate(existingRecord.getBorrowDate());
        updatedDetails.setReturnDate(existingRecord.getReturnDate());

        when(customerService.getCustomerById(1L)).thenReturn(customer);
        when(bookService.getBookById(1L)).thenReturn(book);
        when(borrowingRecordRepository.findById(1L)).thenReturn(Optional.of(existingRecord));
        when(borrowingRecordRepository.save(any(BorrowingRecord.class))).thenReturn(existingRecord);

        BorrowingRecord result = borrowingRecordService.updateBorrowingRecord(1L, 1L, 1L, updatedDetails);
        assertNotNull(result);
        assertEquals(customer, result.getCustomer());
        assertEquals(book, result.getBook());

        ArgumentCaptor<BorrowingRecord> recordCaptor = ArgumentCaptor.forClass(BorrowingRecord.class);
        verify(borrowingRecordRepository, times(1)).save(recordCaptor.capture());
        BorrowingRecord capturedRecord = recordCaptor.getValue();
        assertEquals(customer, capturedRecord.getCustomer());
        assertEquals(book, capturedRecord.getBook());
    }

    @Test
    void updateBorrowingRecord_ShouldThrowException_WhenCustomerBookOrRecordDoesNotExist() {
        BorrowingRecord updatedDetails = new BorrowingRecord();

        when(customerService.getCustomerById(1L)).thenReturn(null);
        when(bookService.getBookById(1L)).thenReturn(new Book());
        when(borrowingRecordRepository.findById(1L)).thenReturn(Optional.of(new BorrowingRecord()));

        assertThrows(RuntimeException.class, () -> borrowingRecordService.updateBorrowingRecord(1L, 1L, 1L, updatedDetails));

        when(customerService.getCustomerById(1L)).thenReturn(new Customer());
        when(bookService.getBookById(1L)).thenReturn(null);
        when(borrowingRecordRepository.findById(1L)).thenReturn(Optional.of(new BorrowingRecord()));

        assertThrows(RuntimeException.class, () -> borrowingRecordService.updateBorrowingRecord(1L, 1L, 1L, updatedDetails));

        when(customerService.getCustomerById(1L)).thenReturn(new Customer());
        when(bookService.getBookById(1L)).thenReturn(new Book());
        when(borrowingRecordRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> borrowingRecordService.updateBorrowingRecord(1L, 1L, 1L, updatedDetails));

        verify(borrowingRecordRepository, never()).save(any(BorrowingRecord.class));
    }

    @Test
    void deleteBorrowingRecord_ShouldDeleteRecord_WhenRecordExists() {
        Long recordId = 1L;

        borrowingRecordService.deleteBorrowingRecord(recordId);

        verify(borrowingRecordRepository, times(1)).deleteById(recordId);
    }

    @Test
    void findByCustomerId_ShouldReturnRecords_WhenCustomerExists() {
        BorrowingRecord record = new BorrowingRecord();
        record.setId(1L);

        when(borrowingRecordRepository.findByCustomerId(1L)).thenReturn(Collections.singletonList(record));

        List<BorrowingRecord> result = borrowingRecordService.findByCustomerId(1L);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());

        verify(borrowingRecordRepository, times(1)).findByCustomerId(1L);
    }

    @Test
    void findByBookId_ShouldReturnRecords_WhenBookExists() {
        BorrowingRecord record = new BorrowingRecord();
        record.setId(1L);

        when(borrowingRecordRepository.findByBookId(1L)).thenReturn(Collections.singletonList(record));

        List<BorrowingRecord> result = borrowingRecordService.findByBookId(1L);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());

        verify(borrowingRecordRepository, times(1)).findByBookId(1L);
    }
}
