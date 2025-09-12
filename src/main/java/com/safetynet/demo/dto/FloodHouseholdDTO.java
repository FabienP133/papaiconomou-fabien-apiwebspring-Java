package com.safetynet.demo.dto;

import java.util.List;

public class FloodHouseholdDTO {
    private String address;
    private List<FirePersonDTO> persons;

    public FloodHouseholdDTO() {}

    public FloodHouseholdDTO(String address, List<FirePersonDTO> persons) {
        this.address = address;
        this.persons = persons;
    }

    public String getAddress() { return address; }
    public List<FirePersonDTO> getPersons() { return persons; }
    public void setAddress(String address) { this.address = address; }
    public void setPersons(List<FirePersonDTO> persons) { this.persons = persons; }
}