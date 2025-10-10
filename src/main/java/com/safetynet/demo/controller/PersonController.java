package com.safetynet.demo.controller;

import com.safetynet.demo.model.Person;
import com.safetynet.demo.service.PersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/person")
public class PersonController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonController.class);

    private final PersonService service;

    public PersonController(PersonService service) {
        this.service = service;
    }


    @PostMapping
    public ResponseEntity<Person> create(@RequestBody Person p) {
        LOGGER.info("create appelé " + p);
        return ResponseEntity.status(201).body(service.create(p));
    }


    @PutMapping
    public ResponseEntity<Person> update(@RequestBody Person p) {
        LOGGER.info("update appelé " + p);
        return ResponseEntity.ok(service.update(p));
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(
            @RequestParam String firstName,
            @RequestParam String lastName) {
        LOGGER.info("delete " + firstName, lastName);
        service.delete(firstName, lastName);
        return ResponseEntity.ok().build();
    }
}