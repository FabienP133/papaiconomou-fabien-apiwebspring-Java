package com.safetynet.demo.dto;

public class ChildAlertHouseholdMemberDTO {
    private String firstName;
    private String lastName;

    public ChildAlertHouseholdMemberDTO() {}

    public ChildAlertHouseholdMemberDTO(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() { return firstName; }
    public String getLastName()  { return lastName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName)   { this.lastName = lastName; }
}