package com.example.LibraryManagementSystem.controller;

import com.example.LibraryManagementSystem.domain.Book;
import com.example.LibraryManagementSystem.domain.BorrowingRecord;
import com.example.LibraryManagementSystem.domain.Customer;
import com.example.LibraryManagementSystem.service.BorrowingRecordService;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class BorrowingRecordControllerTest {

    @Mock
    private BorrowingRecordService borrowingRecordService;

    private BorrowingRecordController borrowingRecordController;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        borrowingRecordController = new BorrowingRecordController(borrowingRecordService);
        mockMvc = MockMvcBuilders.standaloneSetup(borrowingRecordController).build();
    }

    @Test
    void getAllBorrowingRecords_ShouldReturnBorrowingRecordsPage() throws Exception {
        Pageable pageable = PageRequest.of(0, 5);
        BorrowingRecord record = new BorrowingRecord();
        record.setId(1L);
        record.setCustomer(null); // Set customer and book appropriately for your domain
        record.setBook(null);
        Page<BorrowingRecord> borrowingRecordsPage = new PageImpl<>(Collections.singletonList(record));

        when(borrowingRecordService.getAllBorrowingRecords(pageable)).thenReturn(borrowingRecordsPage);

        mockMvc.perform(get("/borrowings")
                        .param("page", "0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(record.getId()));

        verify(borrowingRecordService, times(1)).getAllBorrowingRecords(pageable);
    }

    @Test
    void getBorrowingRecordById_ShouldReturnBorrowingRecord_WhenRecordExists() throws Exception {
        Long recordId = 1L;
        BorrowingRecord record = new BorrowingRecord();
        record.setId(recordId);

        when(borrowingRecordService.getBorrowingRecordById(recordId)).thenReturn(record);

        mockMvc.perform(get("/borrowings/{id}", recordId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(record.getId()));

        verify(borrowingRecordService, times(1)).getBorrowingRecordById(recordId);
    }

    @Test
    void getBorrowingRecordById_ShouldReturnNotFound_WhenRecordDoesNotExist() throws Exception {
        Long nonExistentRecordId = 999L;
        when(borrowingRecordService.getBorrowingRecordById(nonExistentRecordId)).thenReturn(null);

        mockMvc.perform(get("/borrowings/{id}", nonExistentRecordId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("There is no Record with this ID"));

        verify(borrowingRecordService, times(1)).getBorrowingRecordById(nonExistentRecordId);
    }

    @Test
    void createBorrowingRecord_ShouldReturnCreatedRecord() throws Exception {
        BorrowingRecord newRecord = new BorrowingRecord();
        newRecord.setId(1L);
        newRecord.setCustomer(new Customer());
        newRecord.setBook(new Book());

        when(borrowingRecordService.createBorrowingRecord(anyLong(), anyLong(), any(BorrowingRecord.class))).thenReturn(newRecord);

        mockMvc.perform(post("/borrowings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"customer\": {\n" +
                                "    \"id\": 1\n" +
                                "  },\n" +
                                "  \"book\": {\n" +
                                "    \"id\": 1\n" +
                                "  }\n" +
                                "}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(newRecord.getId()));

        ArgumentCaptor<BorrowingRecord> recordCaptor = ArgumentCaptor.forClass(BorrowingRecord.class);
        verify(borrowingRecordService, times(1)).createBorrowingRecord(anyLong(), anyLong(), recordCaptor.capture());
        assertNotNull(recordCaptor.getValue().getCustomer());
        assertNotNull(recordCaptor.getValue().getBook());
    }

    @Test
    void updateBorrowingRecord_ShouldReturnUpdatedRecord_WhenRecordExists() throws Exception {
        Long recordId = 1L;
        BorrowingRecord existingRecord = new BorrowingRecord();
        existingRecord.setId(recordId);
        // Set customer and book appropriately for your domain
        existingRecord.setCustomer(null);
        existingRecord.setBook(null);

        when(borrowingRecordService.updateBorrowingRecord(eq(recordId), anyLong(), anyLong(), any(BorrowingRecord.class))).thenReturn(existingRecord);

        mockMvc.perform(put("/borrowings/{id}", recordId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"customer\": {\n" +
                                "    \"id\": 1\n" +
                                "  },\n" +
                                "  \"book\": {\n" +
                                "    \"id\": 1\n" +
                                "  }\n" +
                                "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingRecord.getId()));

        ArgumentCaptor<BorrowingRecord> recordCaptor = ArgumentCaptor.forClass(BorrowingRecord.class);
        verify(borrowingRecordService, times(1)).updateBorrowingRecord(eq(recordId), anyLong(), anyLong(), recordCaptor.capture());
        assertNotNull(recordCaptor.getValue().getCustomer());
        assertNotNull(recordCaptor.getValue().getBook());
    }

    @Test
    void updateBorrowingRecord_ShouldReturnNotFound_WhenRecordDoesNotExist() throws Exception {
        Long nonExistentRecordId = 999L;
        when(borrowingRecordService.updateBorrowingRecord(eq(nonExistentRecordId), anyLong(), anyLong(), any(BorrowingRecord.class))).thenReturn(null);

        mockMvc.perform(put("/borrowings/{id}", nonExistentRecordId)
                        .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"customer\": {\n" +
                        "    \"id\": 1\n" +
                        "  },\n" +
                        "  \"book\": {\n" +
                        "    \"id\": 1\n" +
                        "  }\n" +
                        "}"))                .andExpect(status().isNotFound());

        verify(borrowingRecordService, times(1)).updateBorrowingRecord(eq(nonExistentRecordId), anyLong(), anyLong(), any(BorrowingRecord.class));
    }

    @Test
    void deleteBorrowingRecord_ShouldDeleteRecord() throws Exception {
        Long recordId = 1L;
        mockMvc.perform(delete("/borrowings/{id}", recordId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(borrowingRecordService, times(1)).deleteBorrowingRecord(recordId);
    }

    @Test
    void searchBorrowingRecords_ShouldReturnBorrowingRecords_WhenSearchParametersValid() throws Exception {
        Long userId = 1L;
        when(borrowingRecordService.findByCustomerId(userId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/borrowings/search")
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(borrowingRecordService, times(1)).findByCustomerId(userId);
    }

    @Test
    void searchBorrowingRecords_ShouldReturnBadRequest_WhenMultipleSearchParameters() throws Exception {
        mockMvc.perform(get("/borrowings/search")
                        .param("userId", "1")
                        .param("bookId", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Please provide exactly one query parameter: userId or bookId"));

        verify(borrowingRecordService, never()).findByCustomerId(anyLong());
        verify(borrowingRecordService, never()).findByBookId(anyLong());
    }
}
