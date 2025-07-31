package com.safetynet.demo.service;

import com.safetynet.demo.model.Person;
import java.util.List;

public interface PersonService {
    List<Person> getAll();
    Person create(Person p);
    Person update(Person p);
    void delete(String firstName, String lastName);
}
