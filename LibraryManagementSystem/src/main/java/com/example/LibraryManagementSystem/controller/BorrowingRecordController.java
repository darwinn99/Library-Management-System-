package com.example.LibraryManagementSystem.controller;

import com.example.LibraryManagementSystem.domain.BorrowingRecord;
import com.example.LibraryManagementSystem.service.BorrowingRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/borrowings")
public class BorrowingRecordController {

    private  BorrowingRecordService borrowingRecordService;

    @Autowired
    public BorrowingRecordController(BorrowingRecordService borrowingRecordService) {
        this.borrowingRecordService = borrowingRecordService;
    }

    @GetMapping
    public List<BorrowingRecord> getAllBorrowingRecords() {
        return borrowingRecordService.getAllBorrowingRecords();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBorrowingRecordById(@PathVariable Long id) {
        BorrowingRecord borrowingRecord = borrowingRecordService.getBorrowingRecordById(id);
        if (borrowingRecord != null) {
            return ResponseEntity.ok(borrowingRecord);
        } else {
            return new ResponseEntity<>("There is no Record with this ID", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping()
    public ResponseEntity<?> createBorrowingRecord(@Valid @RequestBody BorrowingRecord borrowingRecord) {
        try {
            BorrowingRecord createdBorrowingRecord = borrowingRecordService.createBorrowingRecord(borrowingRecord.getCustomer().getId(), borrowingRecord.getBook().getId(), borrowingRecord);
            return ResponseEntity.ok(createdBorrowingRecord);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBorrowingRecord(@PathVariable Long id, @Valid @RequestBody BorrowingRecord borrowingRecord) {
        try {
            BorrowingRecord updatedBorrowingRecord = borrowingRecordService.updateBorrowingRecord(id, borrowingRecord.getCustomer().getId(), borrowingRecord.getBook().getId(), borrowingRecord);
            return ResponseEntity.ok(updatedBorrowingRecord);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBorrowingRecord(@PathVariable Long id) {
        borrowingRecordService.deleteBorrowingRecord(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchBorrowingRecords(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long bookId) {
        int count = 0;
        if (userId != null) count++;
        if (bookId != null) count++;

        if (count != 1) {
            return new ResponseEntity<>("Please provide exactly one query parameter: userId or bookId", HttpStatus.BAD_REQUEST);
        }
        List<BorrowingRecord> borrowingRecords;
        if (userId != null) {
            borrowingRecords = borrowingRecordService.findByCustomerId(userId);
        } else if (bookId != null) {
            borrowingRecords = borrowingRecordService.findByBookId(bookId);
        } else {
            borrowingRecords = borrowingRecordService.getAllBorrowingRecords();
        }
        return ResponseEntity.ok(borrowingRecords);
    }
}
