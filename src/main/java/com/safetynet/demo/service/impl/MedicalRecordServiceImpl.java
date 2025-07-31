package com.safetynet.demo.service.impl;

import com.safetynet.demo.model.MedicalRecord;
import com.safetynet.demo.repository.DataRepository;
import com.safetynet.demo.service.MedicalRecordService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class MedicalRecordServiceImpl implements MedicalRecordService {

    private final DataRepository repository;

    public MedicalRecordServiceImpl(DataRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<MedicalRecord> getAll() {
        return repository.getMedicalrecords();
    }

    @Override
    public MedicalRecord create(MedicalRecord mr) {
        boolean exists = repository.getMedicalrecords().stream().anyMatch(
                x -> x.getFirstName().equalsIgnoreCase(mr.getFirstName())
                        && x.getLastName().equalsIgnoreCase(mr.getLastName())
        );
        if (exists) throw new ResponseStatusException(HttpStatus.CONFLICT);
        return repository.addMedicalRecord(mr);
    }

    @Override
    public MedicalRecord update(MedicalRecord mr) {
        MedicalRecord updated = repository.updateMedicalRecord(mr);
        if (updated == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return updated;
    }

    @Override
    public void delete(String firstName, String lastName) {
        boolean ok = repository.deleteMedicalRecord(firstName, lastName);
        if (!ok) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
}