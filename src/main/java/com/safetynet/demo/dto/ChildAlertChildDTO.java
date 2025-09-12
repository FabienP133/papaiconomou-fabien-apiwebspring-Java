package com.safetynet.demo.dto;

import java.util.List;

public class ChildAlertChildDTO {
    private String firstName;
    private String lastName;
    private int age;
    private List<ChildAlertHouseholdMemberDTO> householdMembers;

    public ChildAlertChildDTO() {}

    public ChildAlertChildDTO(String firstName, String lastName, int age,
                              List<ChildAlertHouseholdMemberDTO> householdMembers) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.householdMembers = householdMembers;
    }

    public String getFirstName() { return firstName; }
    public String getLastName()  { return lastName; }
    public int getAge()          { return age; }
    public List<ChildAlertHouseholdMemberDTO> getHouseholdMembers() { return householdMembers; }

    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName)   { this.lastName = lastName; }
    public void setAge(int age)                { this.age = age; }
    public void setHouseholdMembers(List<ChildAlertHouseholdMemberDTO> householdMembers) {
        this.householdMembers = householdMembers;
    }
}