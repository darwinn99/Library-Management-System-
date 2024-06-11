package com.example.LibraryManagementSystem.repo;

import com.example.LibraryManagementSystem.domain.BorrowingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BorrowingRecordRepository extends JpaRepository<BorrowingRecord, Long> {

    List<BorrowingRecord> findByCustomerId(Long customerId);

    List<BorrowingRecord> findByBookId(Long bookId);
}
