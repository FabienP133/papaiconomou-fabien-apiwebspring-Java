package com.safetynet.demo.controller;

import com.safetynet.demo.model.Person;
import com.safetynet.demo.service.PersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(service.create(p));
        } catch (com.safetynet.demo.exception.ConflictException e) {
            LOGGER.error("Create person conflict: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }


    @PutMapping
    public ResponseEntity<Person> update(@RequestBody Person p) {
        LOGGER.info("update appelé " + p);
        try {
            return ResponseEntity.ok(service.update(p));
        } catch (com.safetynet.demo.exception.NotFoundException e) {
            LOGGER.error("Update person not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(
            @RequestParam String firstName,
            @RequestParam String lastName) {
        LOGGER.info("DELETE /person delete firstName={} lastName={}", firstName, lastName);
        try {
            service.delete(firstName, lastName);
            return ResponseEntity.noContent().build();
        } catch (com.safetynet.demo.exception.NotFoundException e) {
            LOGGER.error("Delete person not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}