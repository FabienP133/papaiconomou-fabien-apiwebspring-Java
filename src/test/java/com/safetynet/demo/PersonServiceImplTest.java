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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        when(repository.addPerson(any())).thenReturn(p);
        Person created = service.create(p);
        assertEquals(p, created);
        verify(repository).addPerson(p);
    }

    @Test
    void create_duplicate_ignoreCase_conflict() {
        Person existing = new Person();
        existing.setFirstName("JOHN");
        existing.setLastName("DOE");
        when(repository.getPersons()).thenReturn(java.util.List.of(existing));

        Person input = new Person();
        input.setFirstName("john");
        input.setLastName("doe");

        assertThrows(com.safetynet.demo.exception.ConflictException.class,
                () -> service.create(input));

        verify(repository).getPersons();
        verify(repository, never()).addPerson(any());
    }

    @Test
    public void testDeletePerson() {
        when(repository.deletePerson("Tom", "Tailor")).thenReturn(true);
        assertDoesNotThrow(() -> service.delete("Tom", "Tailor"));
        verify(repository).deletePerson("Tom", "Tailor");
    }

    @Test
    void delete_notFound_throwsNotFoundException() {
        when(repository.deletePerson(eq("Ghost"), eq("User"))).thenReturn(false);

        assertThrows(com.safetynet.demo.exception.NotFoundException.class,
                () -> service.delete("Ghost", "User"));

        verify(repository).deletePerson("Ghost", "User");
    }

    @Test
    void update_success() {
        Person input = new Person();
        input.setFirstName("John");
        input.setLastName("Boyd");
        input.setAddress("New Addr");


        when(repository.updatePerson(any(Person.class))).thenReturn(input);

        Person res = service.update(input);

        assertNotNull(res);
        assertEquals("John", res.getFirstName());
        assertEquals("Boyd", res.getLastName());
        assertEquals("New Addr", res.getAddress());
    }

    @Test
    void update_notFound_throws404() {
        Person ghost = new Person();
        ghost.setFirstName("Ghost");
        ghost.setLastName("User");
        assertThrows(
                com.safetynet.demo.exception.NotFoundException.class,
                () -> service.update(ghost)
        );
    }

}
