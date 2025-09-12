package com.safetynet.demo.dto;

import java.util.List;

public class FireResponseDTO {
    private String station;
    private List<FirePersonDTO> persons;

    public FireResponseDTO() {}

    public FireResponseDTO(String station, List<FirePersonDTO> persons) {
        this.station = station;
        this.persons = persons;
    }

    public String getStation() { return station; }
    public List<FirePersonDTO> getPersons() { return persons; }
    public void setStation(String station) { this.station = station; }
    public void setPersons(List<FirePersonDTO> persons) { this.persons = persons; }
}