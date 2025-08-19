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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        public void testCreerFireStation() {
            FireStation fs = new FireStation("Address2", "2");
            FireStation created = service.create(fs);
            assertEquals(fs, created);
            verify(repository).addFirestation(fs);
        }

        @Test
        public void testMajSuccess() {
            FireStation fs = new FireStation("Addr4", "4");
            stationList.add(fs);
            FireStation updated = service.update(new FireStation("Addr4", "10"));
            assertEquals("10", updated.getStation());
            verify(repository).saveData();
        }

        @Test
            public void testDelete_Success() {
            FireStation fs = new FireStation("Addr5", "5");
            stationList.add(fs);
            service.delete("Addr5", 5);
            assertTrue(stationList.isEmpty());
            verify(repository).saveData();
        }


}
