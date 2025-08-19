package com.safetynet.demo.repository;

import com.safetynet.demo.model.FireStation;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.demo.model.DataWrapper;
import com.safetynet.demo.model.MedicalRecord;
import com.safetynet.demo.model.Person;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import jakarta.annotation.PostConstruct;

@Component
public class DataRepository {


    private DataWrapper data;
    private Path jsonPath;
    private final ObjectMapper mapper = new ObjectMapper();
    
    @PostConstruct
    public void init() {
        try {
            // charger data.json
            ClassPathResource res = new ClassPathResource("data.json");
            try (InputStream is = res.getInputStream()) {
                data = mapper.readValue(is, DataWrapper.class);
            }
            // garder le chemin du fichier pour pouvoir le réécrire
            File file = res.getFile();
            jsonPath = file.toPath();
        } catch (Exception e) {
            throw new RuntimeException("Impossible de charger data.json", e);
        }
    }


    //Fire Station


    //ajoute une nouvelle FireStation
    public FireStation addFirestation(FireStation fs) {
        data.getFirestations().add(fs);
        saveData();
        return fs;
    }

    //réécrit data.json avec l'état courant de `data`.
    public void saveData() {
        try {
            mapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValue(jsonPath.toFile(), data);
        } catch (Exception e) {
            throw new RuntimeException("Échec de la sauvegarde de data.json", e);
        }
    }


    public List<FireStation> getFirestations() {
        return data.getFirestations();
    }


    public FireStation updateFirestation(FireStation fs) {
        for (int i = 0; i < data.getFirestations().size(); i++) {
            FireStation existing = data.getFirestations().get(i);
            if (existing.getAddress().equalsIgnoreCase(fs.getAddress())) {
                existing.setStation(fs.getStation());
                saveData();
                return existing;
            }
        }
        return null;
    }


    public boolean deleteFirestation(String address, Integer station) {
        boolean removed = getFirestations().removeIf(
                fs -> {
                    return fs.getAddress().equalsIgnoreCase(address) && fs.getStation().equalsIgnoreCase(station.toString());
                }

        );
        return removed;
    }


    //Person

    public List<Person> getPersons() {
        return data.getPersons();
    }

    public Person addPerson(Person p) {
        data.getPersons().add(p);
        saveData();
        return p;
    }


    public Person updatePerson(Person p) {
        List<Person> list = data.getPersons();
        for (int i = 0; i < list.size(); i++) {
            Person existing = list.get(i);
            if (existing.getFirstName().equalsIgnoreCase(p.getFirstName())
                    && existing.getLastName().equalsIgnoreCase(p.getLastName())) {
                list.set(i, p);
                saveData();
                return p;
            }
        }
        return null;
    }

    public boolean deletePerson(String firstName, String lastName) {
        boolean removed = data.getPersons().removeIf(x ->
                x.getFirstName().equalsIgnoreCase(firstName)
                        && x.getLastName().equalsIgnoreCase(lastName));
        if (removed) {
            saveData();
        }
        return removed;
    }



    //Medical Record


    public List<MedicalRecord> getMedicalrecords() {
        return data.getMedicalrecords();
    }

    public MedicalRecord addMedicalRecord(MedicalRecord mr) {
        data.getMedicalrecords().add(mr);
        saveData();
        return mr;
    }

    public MedicalRecord updateMedicalRecord(MedicalRecord mr) {
        List<MedicalRecord> list = data.getMedicalrecords();
        for (int i = 0; i < list.size(); i++) {
            MedicalRecord existing = list.get(i);
            if (existing.getFirstName().equalsIgnoreCase(mr.getFirstName())
                    && existing.getLastName().equalsIgnoreCase(mr.getLastName())) {
                list.set(i, mr);
                saveData();
                return mr;
            }
        }
        return null;
    }

    public boolean deleteMedicalRecord(String firstName, String lastName) {
        boolean removed = data.getMedicalrecords().removeIf(x ->
                x.getFirstName().equalsIgnoreCase(firstName)
                        && x.getLastName().equalsIgnoreCase(lastName));
        if (removed) {
            saveData();
        }
        return removed;
    }

}
