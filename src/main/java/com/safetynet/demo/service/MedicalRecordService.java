package com.safetynet.demo.service;

import com.safetynet.demo.model.MedicalRecord;
import java.util.List;

public interface MedicalRecordService {
    List<MedicalRecord> getAll();
    MedicalRecord create(MedicalRecord mr);
    MedicalRecord update(MedicalRecord mr);
    void delete(String firstName, String lastName);
}