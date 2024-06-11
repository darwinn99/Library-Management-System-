package com.example.LibraryManagementSystem.service;



import com.example.LibraryManagementSystem.domain.Book;
import com.example.LibraryManagementSystem.domain.BorrowingRecord;
import com.example.LibraryManagementSystem.domain.Customer;
import com.example.LibraryManagementSystem.repo.BorrowingRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BorrowingRecordService {

    private BorrowingRecordRepository borrowingRecordRepository;
    private BookService bookService;
    private CustomerService customerService;

    public BorrowingRecordService(BorrowingRecordRepository borrowingRecordRepository, CustomerService customerService, BookService bookService) {
        this.borrowingRecordRepository = borrowingRecordRepository;
        this.customerService = customerService;
        this.bookService = bookService;
    }

    public List<BorrowingRecord> getAllBorrowingRecords() {
        return borrowingRecordRepository.findAll();
    }

    public BorrowingRecord getBorrowingRecordById(Long id) {
        Optional<BorrowingRecord> optionalBorrowingRecord = borrowingRecordRepository.findById(id);
        return optionalBorrowingRecord.orElse(null);
    }

    public BorrowingRecord createBorrowingRecord(Long customerId, Long bookId, BorrowingRecord borrowingRecordDetails) {
        Customer customer = customerService.getCustomerById(customerId);
        Book book = bookService.getBookById(bookId);

        if (customer == null || book == null) {
            throw new RuntimeException("Customer or Book not found");
        }

        borrowingRecordDetails.setCustomer(customer);
        borrowingRecordDetails.setBook(book);
        return borrowingRecordRepository.save(borrowingRecordDetails);
    }

    public BorrowingRecord updateBorrowingRecord(Long id, Long customerId, Long bookId, BorrowingRecord borrowingRecordDetails) {
        Customer customer = customerService.getCustomerById(customerId);
        Book book = bookService.getBookById(bookId);
        Optional<BorrowingRecord> possibleBorrowingRecord = borrowingRecordRepository.findById(id);

        if (customer == null || book == null || possibleBorrowingRecord.isEmpty()) {
            throw new RuntimeException("Customer, Book or BorrowingRecord not found");
        }
        BorrowingRecord borrowingRecord = possibleBorrowingRecord.get();

        borrowingRecord.setCustomer(customer);
        borrowingRecord.setBook(book);
        borrowingRecord.setBorrowDate(borrowingRecordDetails.getBorrowDate());
        borrowingRecord.setReturnDate(borrowingRecordDetails.getReturnDate());
        return borrowingRecordRepository.save(borrowingRecord);
    }


    public void deleteBorrowingRecord(Long id) {
        borrowingRecordRepository.deleteById(id);
    }

    public List<BorrowingRecord> findByCustomerId(Long customerId) {
        return borrowingRecordRepository.findByCustomerId(customerId);
    }

    public List<BorrowingRecord> findByBookId(Long bookId) {
        return borrowingRecordRepository.findByBookId(bookId);
    }
}
