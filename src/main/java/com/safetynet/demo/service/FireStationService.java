package com.safetynet.demo.service;

import com.safetynet.demo.model.FireStation;
import java.util.List;

public interface FireStationService {
    List<FireStation> getAll();
    FireStation create(FireStation fs);
    FireStation update(FireStation fs);
    void delete(String address, int station);
}
