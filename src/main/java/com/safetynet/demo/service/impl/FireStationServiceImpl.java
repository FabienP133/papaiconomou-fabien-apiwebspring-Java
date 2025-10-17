package com.safetynet.demo.service.impl;

import com.safetynet.demo.model.FireStation;
import com.safetynet.demo.repository.DataRepository;
import com.safetynet.demo.service.FireStationService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class FireStationServiceImpl implements FireStationService {

    private final DataRepository repository;

    public FireStationServiceImpl(DataRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<FireStation> getAll() {
        return repository.getFirestations();
    }

    @Override
    public FireStation create(FireStation fs) {
        boolean exists = repository.getFirestations().stream()
                .anyMatch(x -> x.getAddress().equalsIgnoreCase(fs.getAddress()));
        if (exists) {
            throw new com.safetynet.demo.exception.ConflictException(
                    "FireStation existe déjà: " + fs.getAddress());
        }
        return repository.addFirestation(fs);
    }

    @Override
    public FireStation update(FireStation fs) {
        FireStation updated = repository.updateFirestation(fs); // null si inconnue
        if (updated == null) {
            throw new com.safetynet.demo.exception.NotFoundException(
                    "FireStation not found à l'adresse: " + fs.getAddress());
        }
        return updated;
    }

    @Override
    public void delete(String address, int stationNumber) {
        boolean ok = repository.deleteFirestation(address, stationNumber);
        if (!ok) {
            throw new com.safetynet.demo.exception.NotFoundException(
                    "FireStation mapping not found: %s (station %d)".formatted(address, stationNumber));
        }
    }
}
