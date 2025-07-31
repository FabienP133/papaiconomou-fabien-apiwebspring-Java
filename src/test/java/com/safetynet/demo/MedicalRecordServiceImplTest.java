package com.safetynet.demo;

import com.safetynet.demo.model.MedicalRecord;
import com.safetynet.demo.repository.DataRepository;
import com.safetynet.demo.service.impl.MedicalRecordServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MedicalRecordServiceImplTest {

    @Mock
    private DataRepository repository;

    @InjectMocks
    private MedicalRecordServiceImpl service;

    private List<MedicalRecord> recordList;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        recordList = new ArrayList<>();
        when(repository.getMedicalrecords()).thenReturn(recordList);
    }

    @Test
    public void testRetournerRecordList() {
        recordList.add(new MedicalRecord("John", "Doe", "01/01/2000", List.of("med1"), List.of("all1")));
        List<MedicalRecord> result = service.getAll();
        assertEquals(1, result.size());
        verify(repository).getMedicalrecords();
    }

    @Test
    void testCreerRecord() {
        MedicalRecord mr = new MedicalRecord("Jane", "Doe", "02/02/1990", List.of(), List.of());
        MedicalRecord created = service.create(mr);
        assertEquals(mr, created);
        assertTrue(recordList.contains(mr));
        verify(repository).addMedicalRecord(mr);
    }

    @Test
    void testDelete_Success() {
        when(repository.deleteMedicalRecord("Tom", "Tailor")).thenReturn(true);
        assertDoesNotThrow(() -> service.delete("Tom", "Tailor"));
        verify(repository).deleteMedicalRecord("Tom", "Tailor");
    }




}
