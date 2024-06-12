package com.example.LibraryManagementSystem.controller;

import com.example.LibraryManagementSystem.domain.BorrowingRecord;
import com.example.LibraryManagementSystem.service.BorrowingRecordService;
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
@RequestMapping("/borrowings")
public class BorrowingRecordController {

    private final BorrowingRecordService borrowingRecordService;

    @Autowired
    public BorrowingRecordController(BorrowingRecordService borrowingRecordService) {
        this.borrowingRecordService = borrowingRecordService;
    }

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<BorrowingRecord>>> getAllBorrowingRecords(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 5);
        Page<BorrowingRecord> borrowingRecordsPage = borrowingRecordService.getAllBorrowingRecords(pageable);

        List<EntityModel<BorrowingRecord>> borrowingRecords = borrowingRecordsPage.getContent().stream()
                .map(record -> EntityModel.of(record,
                        linkTo(methodOn(BorrowingRecordController.class).getBorrowingRecordById(record.getId())).withSelfRel(),
                        linkTo(methodOn(BorrowingRecordController.class).getAllBorrowingRecords(page)).withRel("borrowings")))
                .collect(Collectors.toList());

        PagedModel<EntityModel<BorrowingRecord>> pagedModel = PagedModel.of(borrowingRecords,
                new PagedModel.PageMetadata(borrowingRecordsPage.getSize(), borrowingRecordsPage.getNumber(), borrowingRecordsPage.getTotalElements()));

        pagedModel.add(linkTo(methodOn(BorrowingRecordController.class).getAllBorrowingRecords(page)).withSelfRel());

        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBorrowingRecordById(@PathVariable Long id) {
        BorrowingRecord borrowingRecord = borrowingRecordService.getBorrowingRecordById(id);
        if (borrowingRecord != null) {
            EntityModel<BorrowingRecord> recordModel = EntityModel.of(borrowingRecord,
                    linkTo(methodOn(BorrowingRecordController.class).getBorrowingRecordById(id)).withSelfRel(),
                    linkTo(methodOn(BorrowingRecordController.class).getAllBorrowingRecords(0)).withRel("borrowings"));
            return ResponseEntity.ok(recordModel);
        } else {
            return new ResponseEntity<>("There is no Record with this ID", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<?> createBorrowingRecord(@Valid @RequestBody BorrowingRecord borrowingRecord) {
        try {
            BorrowingRecord createdBorrowingRecord = borrowingRecordService.createBorrowingRecord(borrowingRecord.getCustomer().getId(), borrowingRecord.getBook().getId(), borrowingRecord);
            EntityModel<BorrowingRecord> recordModel = EntityModel.of(createdBorrowingRecord,
                    linkTo(methodOn(BorrowingRecordController.class).getBorrowingRecordById(createdBorrowingRecord.getId())).withSelfRel(),
                    linkTo(methodOn(BorrowingRecordController.class).getAllBorrowingRecords(0)).withRel("borrowings"));
            return new ResponseEntity<>(recordModel, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBorrowingRecord(@PathVariable Long id, @Valid @RequestBody BorrowingRecord borrowingRecord) {
        try {
            BorrowingRecord updatedBorrowingRecord = borrowingRecordService.updateBorrowingRecord(id, borrowingRecord.getCustomer().getId(), borrowingRecord.getBook().getId(), borrowingRecord);
            EntityModel<BorrowingRecord> recordModel = EntityModel.of(updatedBorrowingRecord,
                    linkTo(methodOn(BorrowingRecordController.class).getBorrowingRecordById(updatedBorrowingRecord.getId())).withSelfRel(),
                    linkTo(methodOn(BorrowingRecordController.class).getAllBorrowingRecords(0)).withRel("borrowings"));
            return ResponseEntity.ok(recordModel);
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
        List<BorrowingRecord> borrowingRecords = List.of();
        if (userId != null) {
            borrowingRecords = borrowingRecordService.findByCustomerId(userId);
        } else if (bookId != null) {
            borrowingRecords = borrowingRecordService.findByBookId(bookId);
        }

        List<EntityModel<BorrowingRecord>> recordModels = borrowingRecords.stream()
                .map(record -> EntityModel.of(record,
                        linkTo(methodOn(BorrowingRecordController.class).getBorrowingRecordById(record.getId())).withSelfRel(),
                        linkTo(methodOn(BorrowingRecordController.class).getAllBorrowingRecords(0)).withRel("borrowings")))
                .collect(Collectors.toList());

        return new ResponseEntity<>(recordModels, HttpStatus.OK);
    }
}
