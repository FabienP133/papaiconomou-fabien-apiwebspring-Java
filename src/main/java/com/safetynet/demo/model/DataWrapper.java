package com.safetynet.demo.model;

import java.util.ArrayList;
import java.util.List;


public class DataWrapper {

    private List<Person> persons = new ArrayList<>();
    private List<FireStation> firestations = new ArrayList<>();
    private List<MedicalRecord> medicalrecords = new ArrayList<>();

    public DataWrapper() {}

    public List<Person> getPersons() {
        return persons;
    }

    public void setPersons(List<Person> persons) {
        this.persons = (persons != null) ? persons : new ArrayList<>();
    }

    public List<FireStation> getFirestations() {
        return firestations;
    }

    public void setFirestations(List<FireStation> firestations) {
        this.firestations = (firestations != null) ? firestations : new ArrayList<>();
    }

    public List<MedicalRecord> getMedicalrecords() {
        return medicalrecords;
    }

    public void setMedicalrecords(List<MedicalRecord> medicalrecords) {
        this.medicalrecords = (medicalrecords != null) ? medicalrecords : new ArrayList<>();
    }
}