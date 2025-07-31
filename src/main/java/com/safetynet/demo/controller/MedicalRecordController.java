package com.safetynet.demo.controller;

import com.safetynet.demo.model.MedicalRecord;
import com.safetynet.demo.service.MedicalRecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medicalRecord")
public class MedicalRecordController {

    private final MedicalRecordService service;

    public MedicalRecordController(MedicalRecordService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<MedicalRecord> create(@RequestBody MedicalRecord mr) {
        return ResponseEntity.status(201).body(service.create(mr));
    }

    @GetMapping
    public ResponseEntity<List<MedicalRecord>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }


    @PutMapping
    public ResponseEntity<MedicalRecord> update(@RequestBody MedicalRecord mr) {
        return ResponseEntity.ok(service.update(mr));
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(
            @RequestParam String firstName,
            @RequestParam String lastName) {
        service.delete(firstName, lastName);
        return ResponseEntity.ok().build();
    }
}