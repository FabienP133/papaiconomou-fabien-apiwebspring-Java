package com.safetynet.demo.service.impl;

import com.safetynet.demo.model.MedicalRecord;
import com.safetynet.demo.repository.DataRepository;
import com.safetynet.demo.service.MedicalRecordService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicalRecordServiceImpl implements MedicalRecordService {

    private final DataRepository repository;

    public MedicalRecordServiceImpl(DataRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<MedicalRecord> getAll() {
        return repository.getMedicalRecords();
    }

    @Override
    public MedicalRecord create(MedicalRecord mr) {
        boolean exists = repository.getMedicalRecords().stream().anyMatch(
                x -> x.getFirstName().equalsIgnoreCase(mr.getFirstName())
                        && x.getLastName().equalsIgnoreCase(mr.getLastName())
        );
        if (exists) {
            throw new com.safetynet.demo.exception.ConflictException(
                    "Le médical record existe déjà pour %s %s"
                            .formatted(mr.getFirstName(), mr.getLastName()));
        }
        return repository.addMedicalRecord(mr);
    }

    @Override
    public MedicalRecord update(MedicalRecord mr) {
        MedicalRecord updated = repository.updateMedicalRecord(mr);
        if (updated == null) throw new com.safetynet.demo.exception.NotFoundException(
                "Medical record introuvable pour %s %s".formatted(mr.getFirstName(), mr.getLastName())
        );
        return updated;
    }

    @Override
    public void delete(String firstName, String lastName) {
        boolean ok = repository.deleteMedicalRecord(firstName, lastName);
        if (!ok) {
            throw new com.safetynet.demo.exception.NotFoundException(
                    "Medical record introuvé pour %s %s".formatted(firstName, lastName)
            );
        }
    }
}