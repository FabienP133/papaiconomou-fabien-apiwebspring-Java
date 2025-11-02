package com.safetynet.demo.controller;

import com.safetynet.demo.model.MedicalRecord;
import com.safetynet.demo.service.MedicalRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/medicalRecord")
public class MedicalRecordController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MedicalRecordController.class);

    private final MedicalRecordService service;

    public MedicalRecordController(MedicalRecordService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<MedicalRecord> create(@RequestBody MedicalRecord mr) {
        LOGGER.info("create appelé " + mr);
        try {
            MedicalRecord created = service.create(mr);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (com.safetynet.demo.exception.ConflictException e) {
            LOGGER.error("Create medical record conflict: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }



    @PutMapping("/{lastName}/{firstName}")
    public ResponseEntity<MedicalRecord> update(@PathVariable String lastName,
                                                @PathVariable String firstName,
                                                @RequestBody MedicalRecord mr) {
        LOGGER.info("PUT /medicalRecord update lastName={} firstName={}", lastName, firstName);
        try {
            mr.setLastName(lastName);
            mr.setFirstName(firstName);
            return ResponseEntity.ok(service.update(mr));
        } catch (com.safetynet.demo.exception.NotFoundException e) {
            LOGGER.error("Update medical record not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{lastName}/{firstName}")
    public ResponseEntity<Void> delete(@PathVariable String lastName,
                                       @PathVariable String firstName) {
        LOGGER.info("DELETE /medicalRecord lastName={} firstName={}", lastName, firstName);
        try {
            service.delete(firstName, lastName);
            return ResponseEntity.noContent().build();
        } catch (com.safetynet.demo.exception.NotFoundException e) {
            LOGGER.error("Delete medical record not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}