package com.safetynet.demo.dto;

import java.util.List;

public class FloodStationsDTO {
    private List<FloodHouseholdDTO> households;

    public FloodStationsDTO() {}

    public FloodStationsDTO(List<FloodHouseholdDTO> households) {
        this.households = households;
    }

    public List<FloodHouseholdDTO> getHouseholds() { return households; }
    public void setHouseholds(List<FloodHouseholdDTO> households) { this.households = households; }
}