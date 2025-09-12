package com.safetynet.demo.dto;

import java.util.List;

public class StationCoverageDTO {
    private List<StationCoveragePersonDTO> persons;
    private int children;
    private int adults;

    public StationCoverageDTO() {}

    public StationCoverageDTO(List<StationCoveragePersonDTO> persons, int children, int adults) {
        this.persons = persons;
        this.children = children;
        this.adults = adults;
    }

    public List<StationCoveragePersonDTO> getPersons() { return persons; }
    public int getChildren() { return children; }
    public int getAdults() { return adults; }

    public void setPersons(List<StationCoveragePersonDTO> persons) { this.persons = persons; }
    public void setChildren(int children) { this.children = children; }
    public void setAdults(int adults) { this.adults = adults; }
}