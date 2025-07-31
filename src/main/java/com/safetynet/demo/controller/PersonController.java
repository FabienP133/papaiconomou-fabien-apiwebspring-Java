package com.safetynet.demo.controller;

import com.safetynet.demo.model.Person;
import com.safetynet.demo.service.PersonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/person")
public class PersonController {

    private final PersonService service;

    public PersonController(PersonService service) {
        this.service = service;
    }


    @PostMapping
    public ResponseEntity<Person> create(@RequestBody Person p) {
        return ResponseEntity.status(201).body(service.create(p));
    }

    @GetMapping
    public ResponseEntity<List<Person>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping
    public ResponseEntity<Person> update(@RequestBody Person p) {
        return ResponseEntity.ok(service.update(p));
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(
            @RequestParam String firstName,
            @RequestParam String lastName) {
        service.delete(firstName, lastName);
        return ResponseEntity.ok().build();
    }
}