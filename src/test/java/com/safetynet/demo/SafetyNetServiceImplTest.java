package com.safetynet.demo;

import com.safetynet.demo.dto.StationCoverageDTO;
import com.safetynet.demo.model.FireStation;
import com.safetynet.demo.model.MedicalRecord;
import com.safetynet.demo.model.Person;
import com.safetynet.demo.repository.DataRepository;
import com.safetynet.demo.service.SafetyNetService;
import com.safetynet.demo.service.impl.SafetyNetServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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


    private static String mmddyyyy(int year) {
        return "01/01/" + year; // format "MM/dd/yyyy"
    }

    @Test
    void getStationCoverage_stationNotFound_returnsEmptyObject() {
        DataRepository repo = Mockito.mock(DataRepository.class);
        when(repo.getFirestations()).thenReturn(List.of()); // aucune station
        SafetyNetService service = new SafetyNetServiceImpl(repo);

        StationCoverageDTO out = service.getStationCoverage(99);

        assertNull(out);
    }

    @Test
    void getStationCoverage_countsAdultsAndChildren_exactContract() {
        DataRepository repo = Mockito.mock(DataRepository.class);

        // 1 station couvrant "1509 Culver St"
        FireStation fs = new FireStation();
        fs.setAddress("1509 Culver St");
        fs.setStation("1");
        when(repo.getFirestations()).thenReturn(List.of(fs));

        // 2 personnes à cette adresse : 1 enfant (~10 ans), 1 adulte (~40 ans)
        Person child = new Person();
        child.setFirstName("Kid");
        child.setLastName("Doe");
        child.setAddress("1509 Culver St");
        child.setPhone("000-111");

        Person adult = new Person();
        adult.setFirstName("Dad");
        adult.setLastName("Doe");
        adult.setAddress("1509 Culver St");
        adult.setPhone("000-222");

        when(repo.getPersons()).thenReturn(List.of(child, adult) );

        MedicalRecord mrChild = new MedicalRecord();
        mrChild.setFirstName("Kid");
        mrChild.setLastName("Doe");
        mrChild.setBirthdate(mmddyyyy(LocalDate.now().getYear() - 10));

        MedicalRecord mrAdult = new MedicalRecord();
        mrAdult.setFirstName("Dad");
        mrAdult.setLastName("Doe");
        mrAdult.setBirthdate(mmddyyyy(LocalDate.now().getYear() - 40));

        when(repo.getMedicalrecords()).thenReturn(List.of(mrChild, mrAdult));

        SafetyNetService service = new SafetyNetServiceImpl(repo);

        StationCoverageDTO out = service.getStationCoverage(1);

        assertNotNull(out);


        assertEquals(2, out.getPersons().size(), "Deux personnes couvertes attendues");
        assertEquals(1, out.getChildren(), "Un enfant attendu");
        assertEquals(1, out.getAdults(), "Un adulte attendu");
    }

    @Test
    void getStationCoverage_missingMedicalRecord_notCounted() {
        DataRepository repo = Mockito.mock(DataRepository.class);

        FireStation fs = new FireStation();
        fs.setAddress("A1");
        fs.setStation("2");
        when(repo.getFirestations()).thenReturn(List.of(fs));

        Person p = new Person();
        p.setFirstName("NoMr");
        p.setLastName("User");
        p.setAddress("A1");
        p.setPhone("123");

        when(repo.getPersons()).thenReturn(List.of(p));
        // Aucun medical record pour cette personne
        when(repo.getMedicalrecords()).thenReturn(List.of());

        SafetyNetService service = new SafetyNetServiceImpl(repo);

        StationCoverageDTO out = service.getStationCoverage(2);

        assertNotNull(out);
        assertEquals(0, out.getChildren());
        assertEquals(0, out.getAdults());


        //List<Map<String, Object>> persons = (List<Map<String, Object>>) out.get("persons");
        assertEquals(1, persons.size(), "La personne doit apparaître dans la liste persons");
    }

    @Test
    void getStationCoverage_ageExactly18_isAdult() {
        DataRepository repo = Mockito.mock(DataRepository.class);

        FireStation fs = new FireStation();
        fs.setAddress("A2");
        fs.setStation("3");
        when(repo.getFirestations()).thenReturn(List.of(fs));

        Person p = new Person();
        p.setFirstName("Eighteen");
        p.setLastName("Now");
        p.setAddress("A2");
        p.setPhone("999");

        when(repo.getPersons()).thenReturn(List.of(p));

        MedicalRecord mr = new MedicalRecord();
        mr.setFirstName("Eighteen");
        mr.setLastName("Now");
        mr.setBirthdate(mmddyyyy(LocalDate.now().getYear() - 18));

        when(repo.getMedicalrecords()).thenReturn(List.of(mr));

        SafetyNetService service = new SafetyNetServiceImpl(repo);

        StationCoverageDTO out = service.getStationCoverage(3);

        assertNotNull(out);
        assertEquals(0, out.getChildren(), "18 ans révolus => adulte");
        assertEquals(1, out.getAdults());
    }

    @Test
    void getStationCoverage_personItems_haveOnlyExpectedFields() {
        DataRepository repo = Mockito.mock(DataRepository.class);

        FireStation fs = new FireStation();
        fs.setAddress("A3");
        fs.setStation("4");
        when(repo.getFirestations()).thenReturn(List.of(fs));

        Person p = new Person();
        p.setFirstName("Only");
        p.setLastName("Four");
        p.setAddress("A3");
        p.setPhone("555-000");

        when(repo.getPersons()).thenReturn(List.of(p));

        MedicalRecord mr = new MedicalRecord();
        mr.setFirstName("Only");
        mr.setLastName("Four");
        mr.setBirthdate(mmddyyyy(LocalDate.now().getYear() - 30));

        when(repo.getMedicalrecords()).thenReturn(List.of(mr));

        SafetyNetService service = new SafetyNetServiceImpl(repo);

        StationCoverageDTO out = service.getStationCoverage(4);

        
        //List<Map<String, Object>> persons = (List<Map<String, Object>>) out.getPersons("persons");
        assertEquals(1, persons.size());

        Map<String, Object> item = (Map<String, Object>) persons.get(0);
        assertEquals(Set.of("firstName", "lastName", "address", "phone"), item.keySet(),
                "Chaque personne doit n'exposer que firstName, lastName, address, phone");
    }

}
