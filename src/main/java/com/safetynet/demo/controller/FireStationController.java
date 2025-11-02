package com.safetynet.demo.controller;

import com.safetynet.demo.model.FireStation;
import com.safetynet.demo.service.FireStationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/firestation")
public class FireStationController {


    private static final Logger LOGGER = LoggerFactory.getLogger(FireStationController.class);

    private FireStationService fireStationService;

    public FireStationController(FireStationService fireStationService) {
        this.fireStationService = fireStationService;
    }


    @PostMapping
    public ResponseEntity<FireStation> createFireStation(@RequestBody FireStation newFs) {
        LOGGER.info("createFireStation appelé " + newFs);
        try {
            FireStation created = fireStationService.create(newFs);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (com.safetynet.demo.exception.ConflictException e) {
            LOGGER.error("Create firestation conflict: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }



    @PutMapping("/{address}/{station}")
    public ResponseEntity<FireStation> updateFireStation(@PathVariable String address,
                                                         @PathVariable int station,
                                                         @RequestBody FireStation updatedFs) {
        LOGGER.info("PUT /firestation update addr={} station={}", address, station);
        try {
            updatedFs.setAddress(address);
            updatedFs.setStation(String.valueOf(station));
            return ResponseEntity.ok(fireStationService.update(updatedFs));
        } catch (com.safetynet.demo.exception.NotFoundException e) {
            LOGGER.error("Update firestation not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{address}/{station}")
    public ResponseEntity<Void> delete(@PathVariable String address,
                                       @PathVariable int station) {
        LOGGER.info("DELETE /firestation addr={} station={}", address, station);
        try {
            fireStationService.delete(address, station);
            return ResponseEntity.noContent().build();
        } catch (com.safetynet.demo.exception.NotFoundException e) {
            LOGGER.error("Delete firestation not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}
