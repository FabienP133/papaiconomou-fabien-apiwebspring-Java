package com.safetynet.demo.service.impl;

import com.safetynet.demo.model.Person;
import com.safetynet.demo.repository.DataRepository;
import com.safetynet.demo.service.PersonService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonServiceImpl implements PersonService {

    private final DataRepository repository;

    public PersonServiceImpl(DataRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Person> getAll() {
        return repository.getPersons();
    }

    @Override
    public Person create(Person p) {
        boolean exists = repository.getPersons().stream().anyMatch(x ->
                x.getFirstName().equalsIgnoreCase(p.getFirstName())
                        && x.getLastName().equalsIgnoreCase(p.getLastName())
        );
        if (exists) {
            throw new com.safetynet.demo.exception.ConflictException(
                    "La personne existe déjà : %s %s".formatted(p.getFirstName(), p.getLastName()));
        }
        return repository.addPerson(p);
    }

    @Override
    public Person update(Person p) {
        Person updated = repository.updatePerson(p); // peut renvoyer null si introuvable
        if (updated == null) {
            throw new com.safetynet.demo.exception.NotFoundException(
                    "Peronne pas trouvée : %s %s".formatted(p.getFirstName(), p.getLastName()));
        }
        return updated;
    }

    @Override
    public void delete(String firstName, String lastName) {
        boolean ok = repository.deletePerson(firstName, lastName);
        if (!ok) {
            throw new com.safetynet.demo.exception.NotFoundException(
                    "La personne n'existe pas : %s %s".formatted(firstName, lastName));
        }
    }
}