package com.safetynet.demo;

import com.safetynet.demo.model.DataWrapper;
import com.safetynet.demo.model.FireStation;
import com.safetynet.demo.model.MedicalRecord;
import com.safetynet.demo.model.Person;
import com.safetynet.demo.repository.DataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DataRepositoryTest {

    private DataRepository repo;

    @BeforeEach
    void setup() {
        // Spy du repo réel pour exécuter la logique réelle
        repo = Mockito.spy(new DataRepository());

        //  Initialisation du wrapper interne pour éviter data == null
        ReflectionTestUtils.setField(repo, "data", new DataWrapper());

        //  Pas d’Input/Output disque pendant les tests
        Mockito.doNothing().when(repo).saveData();
    }

    @Test
    void addUpdateDelete_person() {
        // ADD
        Person alice = new Person("Alice","Wonder","B","C","2","phone","mail");
        Person added = repo.addPerson(alice);
        assertNotNull(added);
        assertTrue(repo.getPersons().stream()
                .anyMatch(p -> "Alice".equals(p.getFirstName()) && "Wonder".equals(p.getLastName())));

        // UPDATE (retourne Person)
        added.setAddress("B2");
        Person updated = repo.updatePerson(added);
        assertNotNull(updated);
        assertEquals("B2", updated.getAddress());

        // DELETE (retourne boolean)
        boolean deleted = repo.deletePerson("Alice", "Wonder");
        assertTrue(deleted);
        assertFalse(repo.getPersons().stream()
                .anyMatch(p -> "Alice".equals(p.getFirstName()) && "Wonder".equals(p.getLastName())));
    }

    @Test
    void addUpdateDelete_firestation() {
        // ADD
        FireStation fs = new FireStation("B", "2");
        FireStation added = repo.addFirestation(fs);
        assertNotNull(added);
        assertTrue(repo.getFirestations().stream()
                .anyMatch(s -> "B".equals(s.getAddress()) && "2".equals(s.getStation())));

        // UPDATE (retourne FireStation)
        added.setStation("3");
        FireStation updated = repo.updateFirestation(added);
        assertNotNull(updated);
        assertEquals("3", updated.getStation());

        // DELETE (retourne boolean)
        boolean deleted = repo.deleteFirestation("B", 3);
        assertTrue(deleted);
        assertFalse(repo.getFirestations().stream()
                .anyMatch(s -> "B".equals(s.getAddress()) && "3".equals(s.getStation())));
    }

    @Test
    void addUpdateDelete_medicalRecord() {
        // ADD
        MedicalRecord mr = new MedicalRecord("Bob","Lee","02/02/1990", List.of("m"), List.of());
        MedicalRecord added = repo.addMedicalRecord(mr);
        assertNotNull(added);
        assertTrue(repo.getMedicalrecords().stream()
                .anyMatch(x -> "Bob".equals(x.getFirstName()) && "Lee".equals(x.getLastName())));

        // UPDATE (retourne MedicalRecord)
        added.setMedications(List.of("n"));
        MedicalRecord updated = repo.updateMedicalRecord(added);
        assertNotNull(updated);
        assertEquals(List.of("n"), updated.getMedications());

        // DELETE (retourne boolean)
        boolean deleted = repo.deleteMedicalRecord("Bob", "Lee");
        assertTrue(deleted);
        assertFalse(repo.getMedicalrecords().stream()
                .anyMatch(x -> "Bob".equals(x.getFirstName()) && "Lee".equals(x.getLastName())));
    }

}
