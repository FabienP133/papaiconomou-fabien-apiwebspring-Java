package com.safetynet.demo;

import com.safetynet.demo.model.FireStation;
import com.safetynet.demo.model.Person;
import com.safetynet.demo.repository.DataRepository;
import com.safetynet.demo.service.impl.SafetyNetServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SafetyNetServiceImplTest {


    @Mock
    private DataRepository repository;

    @InjectMocks
    private SafetyNetServiceImpl service;

    private List<FireStation> firestations;
    private List<Person> persons;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        firestations = new ArrayList<>();
        persons = new ArrayList<>();
        when(repository.getFirestations()).thenReturn(firestations);
        when(repository.getPersons()).thenReturn(persons);
    }

    private static Person person(String fn, String ln, String address, String city, String zip, String phone, String email) {
        Person p = new Person();
        p.setFirstName(fn);
        p.setLastName(ln);
        p.setAddress(address);
        p.setCity(city);
        p.setZip(zip);
        p.setPhone(phone);
        p.setEmail(email);
        return p;
    }

    @Test
    void getPhoneAlert_returnsUniquePhonesForStation() {
        // Firestations
        FireStation fs1 = new FireStation(); fs1.setAddress("1509 Culver St"); fs1.setStation("1");
        FireStation fs2 = new FireStation(); fs2.setAddress("29 15th St");    fs2.setStation("1");
        FireStation fs3 = new FireStation(); fs3.setAddress("748 Townings Dr"); fs3.setStation("2");
        firestations.add(fs1); firestations.add(fs2); firestations.add(fs3);

        // Persons
        persons.add(person("John","Boyd","1509 Culver St","Culver","97451","841-874-6512","john@mail.com"));
        persons.add(person("Jacob","Boyd","1509 Culver St","Culver","97451","841-874-6512","jacob@mail.com")); // meme tel -> dedup
        persons.add(person("Tenley","Boyd","29 15th St","Culver","97451","841-874-6513","tenley@mail.com"));
        persons.add(person("Roger","Boyd","748 Townings Dr","Culver","97451","841-874-6514","roger@mail.com")); // station 2 -> exclu

        var phones = service.getPhoneAlert(1);
        assertEquals(2, phones.size());
        assertTrue(phones.contains("841-874-6512"));
        assertTrue(phones.contains("841-874-6513"));
    }

    @Test
    void getPhoneAlert_returnsEmptyWhenNoMatch() {
        //Pas de concordance fs/person à la station 9
        var phones = service.getPhoneAlert(9);
        assertNotNull(phones);
        assertTrue(phones.isEmpty());
    }


    @Test
    void getCommunityEmail_returnsEmailsForCityDistinct() {
        persons.add(person("John","Boyd","1509 Culver St","Culver","97451",null,"john@mail.com"));
        persons.add(person("Jacob","Boyd","1509 Culver St","Culver","97451",null,"john@mail.com")); // email dedup
        persons.add(person("Tenley","Boyd","29 15th St","Culver","97451",null,"tenley@mail.com"));
        persons.add(person("Tim","Dunc","Nope","OtherCity","00000",null,"tim@mail.com")); // autre ville

        var emails = service.getCommunityEmail("Culver");
        assertEquals(2, emails.size());
        assertTrue(emails.contains("john@mail.com"));
        assertTrue(emails.contains("tenley@mail.com"));
    }


    @Test
    void getCommunityEmail_returnsEmptyWhenNoCityMatch() {
        var emails = service.getCommunityEmail("Nowhere");
        assertNotNull(emails);
        assertTrue(emails.isEmpty());
    }

}
