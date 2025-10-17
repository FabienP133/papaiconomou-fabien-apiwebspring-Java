package com.safetynet.demo;

import com.safetynet.demo.dto.*;
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

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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

        when(repo.getMedicalRecords()).thenReturn(List.of(mrChild, mrAdult));

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
        when(repo.getMedicalRecords()).thenReturn(List.of());

        SafetyNetService service = new SafetyNetServiceImpl(repo);

        StationCoverageDTO out = service.getStationCoverage(2);

        assertNotNull(out);
        assertEquals(0, out.getChildren());
        assertEquals(0, out.getAdults());


        //  on lit via le DTO (plus via une Map)
        List<StationCoveragePersonDTO> persons = out.getPersons();
        assertEquals(1, persons.size(), "La personne doit apparaître dans la liste persons");

        StationCoveragePersonDTO item = persons.get(0);
        assertEquals("NoMr", item.getFirstName());
        assertEquals("User", item.getLastName());
        assertEquals("A1",   item.getAddress());
        assertEquals("123",  item.getPhone());
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

        when(repo.getMedicalRecords()).thenReturn(List.of(mr));

        SafetyNetService service = new SafetyNetServiceImpl(repo);

        StationCoverageDTO out = service.getStationCoverage(3);

        assertNotNull(out);
        assertEquals(0, out.getChildren(), "18 ans révolus => adulte");
        assertEquals(1, out.getAdults());
    }

    @Test
    void getStationCoverage_personItems_haveOnlyExpectedFields() {
        DataRepository repo = Mockito.mock(DataRepository.class);

        // Station "4" couvre l'adresse A3
        FireStation fs = new FireStation();
        fs.setAddress("A3");
        fs.setStation("4");
        when(repo.getFirestations()).thenReturn(List.of(fs));

        // Une personne à A3
        Person p = new Person();
        p.setFirstName("Only");
        p.setLastName("Four");
        p.setAddress("A3");
        p.setPhone("555-000");
        when(repo.getPersons()).thenReturn(List.of(p));

        // Dossier médical (âge quelconque, pas utilisé pour la liste persons)
        MedicalRecord mr = new MedicalRecord();
        mr.setFirstName("Only");
        mr.setLastName("Four");
        mr.setBirthdate(mmddyyyy(LocalDate.now().getYear() - 30));
        when(repo.getMedicalRecords()).thenReturn(List.of(mr));

        SafetyNetService service = new SafetyNetServiceImpl(repo);

        StationCoverageDTO out = service.getStationCoverage(4);
        assertNotNull(out);

        //  lire via le DTO (plus de Map)
        List<StationCoveragePersonDTO> persons = out.getPersons();
        assertNotNull(persons);
        assertEquals(1, persons.size(), "La personne doit apparaître dans la liste persons");

        StationCoveragePersonDTO item = persons.get(0);
        assertEquals("Only", item.getFirstName());
        assertEquals("Four", item.getLastName());
        assertEquals("A3", item.getAddress());
        assertEquals("555-000", item.getPhone());

        //  Vérifie que le DTO n'expose QUE les 4 champs attendus
        Set<String> fieldNames = Arrays.stream(StationCoveragePersonDTO.class.getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toSet());
        assertEquals(Set.of("firstName", "lastName", "address", "phone"), fieldNames,
                "Chaque personne doit n'exposer que firstName, lastName, address, phone");
    }

    @Test
    void getFire_success() {
        when(repository.getFirestations()).thenReturn(List.of(new FireStation("1509 Culver St", "3")));

        Person p1 = new Person(); p1.setFirstName("John");  p1.setLastName("Boyd"); p1.setAddress("1509 Culver St"); p1.setPhone("111");
        Person p2 = new Person(); p2.setFirstName("Jacob"); p2.setLastName("Boyd"); p2.setAddress("1509 Culver St"); p2.setPhone("222");
        when(repository.getPersons()).thenReturn(List.of(p1, p2));

        MedicalRecord mr1 = new MedicalRecord(); mr1.setFirstName("John");  mr1.setLastName("Boyd");
        mr1.setBirthdate(mmddyyyy(LocalDate.now().getYear() - 41)); mr1.setMedications(List.of("aznol:200mg")); mr1.setAllergies(List.of("nillacilan"));
        MedicalRecord mr2 = new MedicalRecord(); mr2.setFirstName("Jacob"); mr2.setLastName("Boyd");
        mr2.setBirthdate(mmddyyyy(LocalDate.now().getYear() - 32)); mr2.setMedications(List.of()); mr2.setAllergies(List.of());
        when(repository.getMedicalRecords()).thenReturn(List.of(mr1, mr2));

        FireResponseDTO out = service.getFire("1509 Culver St");

        assertNotNull(out);
        assertEquals("3", out.getStation());
        assertEquals(2, out.getPersons().size());
        assertEquals("John", out.getPersons().get(0).getFirstName());
    }

    @Test
    void getFire_unknownAddress_returnsNull() {
        when(repository.getFirestations()).thenReturn(List.of()); // aucune mapping
        assertNull(service.getFire("Unknown"));
    }

    @Test
    void getPersonInfo_byLastName() {
        Person p1 = new Person(); p1.setFirstName("Ten");  p1.setLastName("Doe"); p1.setAddress("A"); p1.setEmail("ten@x");
        Person p2 = new Person(); p2.setFirstName("Forty"); p2.setLastName("Doe"); p2.setAddress("B"); p2.setEmail("forty@x");
        Person p3 = new Person(); p3.setFirstName("Other"); p3.setLastName("Else");
        when(repository.getPersons()).thenReturn(List.of(p1, p2, p3));

        MedicalRecord m1 = new MedicalRecord(); m1.setFirstName("Ten");   m1.setLastName("Doe"); m1.setBirthdate(mmddyyyy(LocalDate.now().getYear() - 10));
        MedicalRecord m2 = new MedicalRecord(); m2.setFirstName("Forty"); m2.setLastName("Doe"); m2.setBirthdate(mmddyyyy(LocalDate.now().getYear() - 40));
        when(repository.getMedicalRecords()).thenReturn(List.of(m1, m2));

        List<PersonInfoDTO> out = service.getPersonInfo("doe");

        assertEquals(2, out.size());
        assertTrue(out.stream().anyMatch(i -> i.getFirstName().equals("Ten")   && i.getAge() >= 9 && i.getAge() <= 11));
        assertTrue(out.stream().anyMatch(i -> i.getFirstName().equals("Forty") && i.getAge() >= 39 && i.getAge() <= 41));
    }

    @Test
    void getChildAlert_childAndAdults() {
        Person kid = new Person(); kid.setFirstName("Kid"); kid.setLastName("Smith"); kid.setAddress("A1");
        Person mom = new Person(); mom.setFirstName("Anna"); mom.setLastName("Smith"); mom.setAddress("A1");
        when(repository.getPersons()).thenReturn(List.of(kid, mom));

        MedicalRecord mk = new MedicalRecord(); mk.setFirstName("Kid");  mk.setLastName("Smith");  mk.setBirthdate(mmddyyyy(LocalDate.now().getYear() - 10));
        MedicalRecord mm = new MedicalRecord(); mm.setFirstName("Anna"); mm.setLastName("Smith");  mm.setBirthdate(mmddyyyy(LocalDate.now().getYear() - 35));
        when(repository.getMedicalRecords()).thenReturn(List.of(mk, mm));

        List<ChildAlertChildDTO> out = service.getChildAlert("A1");

        assertEquals(1, out.size());
        assertEquals("Kid", out.get(0).getFirstName());
        assertEquals(1, out.get(0).getHouseholdMembers().size()); // la mère
    }

    @Test
    void getChildAlert_noChildren_returnsEmptyList() {
        Person a = new Person(); a.setFirstName("A"); a.setLastName("X"); a.setAddress("A2");
        Person b = new Person(); b.setFirstName("B"); b.setLastName("X"); b.setAddress("A2");
        when(repository.getPersons()).thenReturn(List.of(a, b));

        MedicalRecord mra = new MedicalRecord(); mra.setFirstName("A"); mra.setLastName("X"); mra.setBirthdate(mmddyyyy(LocalDate.now().getYear() - 30));
        MedicalRecord mrb = new MedicalRecord(); mrb.setFirstName("B"); mrb.setLastName("X"); mrb.setBirthdate(mmddyyyy(LocalDate.now().getYear() - 25));
        when(repository.getMedicalRecords()).thenReturn(List.of(mra, mrb));

        assertTrue(service.getChildAlert("A2").isEmpty());
    }

    @Test
    void getFloodStations_success() {
        FireStation s1a1 = new FireStation("A1", "1");
        FireStation s1a2 = new FireStation("A2", "1");
        when(repository.getFirestations()).thenReturn(List.of(s1a1, s1a2));

        Person p1 = new Person(); p1.setFirstName("P1"); p1.setLastName("L1"); p1.setAddress("A1"); p1.setPhone("111");
        Person p2 = new Person(); p2.setFirstName("P2"); p2.setLastName("L2"); p2.setAddress("A2"); p2.setPhone("222");
        when(repository.getPersons()).thenReturn(List.of(p1, p2));

        MedicalRecord m1 = new MedicalRecord(); m1.setFirstName("P1"); m1.setLastName("L1"); m1.setBirthdate(mmddyyyy(LocalDate.now().getYear() - 20));
        MedicalRecord m2 = new MedicalRecord(); m2.setFirstName("P2"); m2.setLastName("L2"); m2.setBirthdate(mmddyyyy(LocalDate.now().getYear() - 10));
        when(repository.getMedicalRecords()).thenReturn(List.of(m1, m2));

        FloodStationsDTO out = service.getFloodStations(List.of(1));

        assertNotNull(out);
        assertFalse(out.getHouseholds().isEmpty());
        // Vérifie qu'au moins une adresse attendue est présente
        assertTrue(out.getHouseholds().stream().anyMatch(h -> h.getAddress().equals("A1") || h.getAddress().equals("A2")));
        // Et qu'il y a bien des personnes listées
        assertTrue(out.getHouseholds().stream().anyMatch(h -> !h.getPersons().isEmpty()));
    }

}
