package com.safetynet.demo.service.impl;

import com.safetynet.demo.dto.PersonInfoDTO;
import com.safetynet.demo.repository.DataRepository;
import com.safetynet.demo.service.SafetyNetService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SafetyNetServiceImpl implements SafetyNetService {

    private final DataRepository repo;

    public SafetyNetServiceImpl(DataRepository repo) {
        this.repo = repo;
    }

    //implémentations

    @Override
    public List<String> getPhoneAlert(int stationNumber) {
        return List.of();
    }

    @Override
    public List<Object> getFire(String address) {
        return List.of().reversed();
    }


    @Override
    public List<PersonInfoDTO> getPersonInfo(String lastName) {
        return List.of();
    }

    @Override
    public List<String> getCommunityEmail(String city) {
        return List.of();
    }
}
