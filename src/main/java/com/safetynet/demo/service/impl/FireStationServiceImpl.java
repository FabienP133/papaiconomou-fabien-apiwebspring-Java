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
                .anyMatch(e -> e.getAddress().equalsIgnoreCase(fs.getAddress()));
        if (exists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Adresse déjà existante");
        }
        repository.addFirestation(fs);
        return fs;
    }

    @Override
    public FireStation update(FireStation fs) {
        Optional<FireStation> existing = repository.getFirestations().stream()
                .filter(e -> e.getAddress().equalsIgnoreCase(fs.getAddress()))
                .findFirst();
        if (existing.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Adresse non trouvée");
        }
        FireStation toUpdate = existing.get();
        toUpdate.setStation(fs.getStation());
        repository.saveData();
        return toUpdate;
    }

    @Override
    public void delete(String address, Integer station) {
        boolean removed = repository.getFirestations().removeIf(
                fs -> {
                    return fs.getAddress().equalsIgnoreCase(address) && fs.getStation().equalsIgnoreCase(station.toString());
                }

        );
        if (!removed) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entrée non trouvée");
        }
        repository.saveData();
    }
}
