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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    public void testGetAll() {
        recordList.add(new MedicalRecord("John", "Doe", "01/01/2000", List.of("med1"), List.of("all1")));
        List<MedicalRecord> result = service.getAll();
        assertEquals(1, result.size());
        verify(repository).getMedicalrecords();
    }

    @Test
    void testCreerRecord() {
        MedicalRecord mr = new MedicalRecord("Jane", "Doe", "02/02/1990", List.of(), List.of());
        when(repository.addMedicalRecord(any())).thenReturn(mr);
        MedicalRecord created = service.create(mr);
        assertEquals(mr, created);
        verify(repository).addMedicalRecord(mr);
    }

    @Test
    void testDelete_Success() {
        when(repository.deleteMedicalRecord("Tom", "Tailor")).thenReturn(true);
        assertDoesNotThrow(() -> service.delete("Tom", "Tailor"));
        verify(repository).deleteMedicalRecord("Tom", "Tailor");
    }

    @Test
    void update_success() {
        MedicalRecord in  = new MedicalRecord("Jane","Doe","02/02/1990", List.of("med:2"), List.of());
        MedicalRecord out = new MedicalRecord("Jane","Doe","02/02/1990", List.of("med:2"), List.of());
        when(repository.getMedicalrecords()).thenReturn(List.of(in));
        when(repository.updateMedicalRecord(in)).thenReturn(out);

        MedicalRecord res = service.update(in);

        assertNotNull(res);
        assertEquals("Jane", res.getFirstName());
        verify(repository).updateMedicalRecord(in);
    }

    @Test
    void create_duplicate_ignoreCase_conflict() {
        var existing = new MedicalRecord("JANE","DOE","02/02/1990", List.of(), List.of());
        when(repository.getMedicalrecords()).thenReturn(List.of(existing));

        var input = new MedicalRecord("jane","doe","02/02/1990", List.of(), List.of());

        var ex = assertThrows(org.springframework.web.server.ResponseStatusException.class,
                () -> service.create(input));

        assertEquals(org.springframework.http.HttpStatus.CONFLICT, ex.getStatusCode());
        verify(repository, never()).addMedicalRecord(any());
    }

    @Test
    void create_new_ok() {
        when(repository.getMedicalrecords()).thenReturn(List.of());
        var input = new MedicalRecord("Alice","Wonder","01/01/2000", List.of("aspirin:100mg"), List.of());
        when(repository.addMedicalRecord(any())).thenAnswer(inv -> inv.getArgument(0));

        MedicalRecord res = service.create(input);

        assertNotNull(res);
        assertEquals("Alice", res.getFirstName());
        verify(repository).addMedicalRecord(any());
    }








}
