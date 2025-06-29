package com.safetynet.demo.controller;

import com.safetynet.demo.model.FireStation;
import com.safetynet.demo.repository.DataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/firestation")
public class FireStationController {

    private final DataRepository repository;

    @Autowired
    public FireStationController(DataRepository dataRepository) {
        this.repository = dataRepository;
    }

    //POST

    @PostMapping
    public ResponseEntity<FireStation> createFireStation(@RequestBody FireStation newFs) {
        // Récupère la liste en mémoire
        List<FireStation> stations = repository.getFirestations();

        // Vérifie si l'adresse existe déjà
        boolean exists = stations.stream()
                .anyMatch(fs -> fs.getAddress().equalsIgnoreCase(newFs.getAddress()));
        if (exists) {
            // S'il y a un doublon, on renvoie un json vide + 409 Conflict
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(null);
        }

        // Sinon on ajoute et on renvoie 201 Created
        stations.add(newFs);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(newFs);
    }

    //récup toutes les adresses des firestations
    @GetMapping
    public ResponseEntity<List<FireStation>> getFirestations() {
        List<FireStation> stations = repository.getFirestations();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(stations);
    }

     //PUT

    @PutMapping
    public ResponseEntity<?> updateFireStation(@RequestBody FireStation updatedFs) {
        List<FireStation> stations = repository.getFirestations();

        // Je cherche la caserne par adresse
        Optional<FireStation> existingOpt = stations.stream()
                .filter(fs -> fs.getAddress().equalsIgnoreCase(updatedFs.getAddress()))
                .findFirst();

        if (existingOpt.isPresent()) {
            // Maj du numéro de station
            FireStation existing = existingOpt.get();
            existing.setStation(updatedFs.getStation());
            return ResponseEntity.ok(existing);
        } else {
            // adresse non trouvée : on renvoie un json vide avec l'erreur 404 Not Found
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Collections.emptyMap());
        }

    }

}
