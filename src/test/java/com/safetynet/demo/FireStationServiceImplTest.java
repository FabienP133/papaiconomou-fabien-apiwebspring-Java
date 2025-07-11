package com.safetynet.demo;

import com.safetynet.demo.model.FireStation;
import com.safetynet.demo.repository.DataRepository;
import com.safetynet.demo.service.FireStationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
        public void testRetournerList() {
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

}
