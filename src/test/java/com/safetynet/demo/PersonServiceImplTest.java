package com.safetynet.demo;

import com.safetynet.demo.model.Person;
import com.safetynet.demo.repository.DataRepository;
import com.safetynet.demo.service.impl.PersonServiceImpl;
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

public class PersonServiceImplTest {

    @Mock
    private DataRepository repository;

    @InjectMocks
    private PersonServiceImpl service;

    private List<Person> personList;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        personList = new ArrayList<>();
        when(repository.getPersons()).thenReturn(personList);
    }

    @Test
    public void testRetournerPersonList() {
        personList.add(new Person("John", "Doe", null, null, null, null, null));
        List<Person> result = service.getAll();
        assertEquals(1, result.size());
        verify(repository).getPersons();
    }

    @Test
    public void testCreerPerson() {
        Person p = new Person("Jane", "Doe", "1 rue A", "City", "00000", "1234", "jane@x.com");
        Person created = service.create(p);
        assertEquals(p, created);
        assertTrue(personList.contains(p));
        verify(repository).addPerson(p);
    }

    @Test
    public void testDeletePerson() {
        when(repository.deletePerson("Tom", "Tailor")).thenReturn(true);
        assertDoesNotThrow(() -> service.delete("Tom", "Tailor"));
        verify(repository).deletePerson("Tom", "Tailor");
    }


}
