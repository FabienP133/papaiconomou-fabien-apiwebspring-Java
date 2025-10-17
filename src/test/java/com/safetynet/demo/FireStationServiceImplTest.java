package com.safetynet.demo;

import com.safetynet.demo.model.FireStation;
import com.safetynet.demo.repository.DataRepository;
import com.safetynet.demo.service.impl.FireStationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class FireStationServiceImplTest {


        @Mock
        private DataRepository repository;

        @InjectMocks
        private FireStationServiceImpl service;

        private List<FireStation> stationList;

        @BeforeEach
        public void setUp() {
            MockitoAnnotations.openMocks(this);
            stationList = new ArrayList<>();
            when(repository.getFirestations()).thenReturn(stationList);
        }

        @Test
        public void testRetournerFirestationsList() {
            stationList.add(new FireStation("Address1", "1"));
            List<FireStation> result = service.getAll();
            assertEquals(1, result.size());
            verify(repository).getFirestations();
        }

    @Test
    void testCreerFireStation() {
        FireStation fs = new FireStation("Address2", "2");

        when(repository.getFirestations()).thenReturn(java.util.Collections.emptyList());
        when(repository.addFirestation(any(FireStation.class)))
                .thenAnswer(inv -> inv.getArgument(0, FireStation.class));

        FireStation created = service.create(fs);

        assertNotNull(created);
        assertEquals("Address2", created.getAddress());
        assertEquals("2", created.getStation());
        verify(repository).getFirestations();
        verify(repository).addFirestation(fs);
        verifyNoMoreInteractions(repository);
    }

        @Test
        public void testMajSuccess() {
            FireStation fs = new FireStation("Addr4", "4");
            when(repository.updateFirestation(any())).thenReturn(fs);
            FireStation updated = service.update(new FireStation("Addr4", "4"));
            assertEquals("4", updated.getStation());
        }

        @Test
        public void testMajError() {
            when(repository.updateFirestation(any(FireStation.class))).thenReturn(null);
            assertThrows(com.safetynet.demo.exception.NotFoundException.class,
                    ()-> service.update(new FireStation("Addr5", "11")));
        }

    @Test
    void testDelete_Success() {
        when(repository.deleteFirestation(eq("Addr5"), eq(5))).thenReturn(true);

        service.delete("Addr5", 5);

        verify(repository).deleteFirestation("Addr5", 5);
        verifyNoMoreInteractions(repository); // pas de saveData ici
    }

    @Test
    void testDelete_NotFound() {
        when(repository.deleteFirestation(eq("X"), eq(9))).thenReturn(false);

        assertThrows(com.safetynet.demo.exception.NotFoundException.class,
                () -> service.delete("X", 9));

        verify(repository).deleteFirestation("X", 9);
        verifyNoMoreInteractions(repository);
    }


}
