package com.safetynet.demo.dto;

import java.util.List;

public class FirePersonDTO {
    private String firstName;
    private String lastName;
    private String phone;
    private int age;
    private List<String> medications;
    private List<String> allergies;

    public FirePersonDTO() {}

    public FirePersonDTO(String firstName, String lastName, String phone, int age,
                         List<String> medications, List<String> allergies) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.age = age;
        this.medications = medications;
        this.allergies = allergies;
    }

    public String getFirstName() { return firstName; }
    public String getLastName()  { return lastName; }
    public String getPhone()     { return phone; }
    public int getAge()          { return age; }
    public List<String> getMedications() { return medications; }
    public List<String> getAllergies()   { return allergies; }

    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName)   { this.lastName = lastName; }
    public void setPhone(String phone)         { this.phone = phone; }
    public void setAge(int age)                { this.age = age; }
    public void setMedications(List<String> medications) { this.medications = medications; }
    public void setAllergies(List<String> allergies)     { this.allergies = allergies; }
}