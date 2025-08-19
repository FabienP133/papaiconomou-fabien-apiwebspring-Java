package com.safetynet.demo.service.impl;

import com.safetynet.demo.dto.PersonInfoDTO;
import com.safetynet.demo.model.FireStation;
import com.safetynet.demo.model.Person;
import com.safetynet.demo.repository.DataRepository;
import com.safetynet.demo.service.SafetyNetService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class SafetyNetServiceImpl implements SafetyNetService {

    private final DataRepository repository;

    public SafetyNetServiceImpl(DataRepository repository) {
        this.repository = repository;
    }

    //implémentations

    @Override
    public List<String> getPhoneAlert(int stationNumber) {
        String stationStr = String.valueOf(stationNumber);

        return repository.getFirestations().stream()
                .filter(fs -> stationStr.equals(fs.getStation()))   //  compare String à String
                .map(FireStation::getAddress)
                .flatMap(addr -> repository.getPersons().stream()
                        .filter(p -> addr.equalsIgnoreCase(p.getAddress()))
                        .map(Person::getPhone))
                .filter(Objects::nonNull)
                .filter(s -> !s.isBlank()).distinct().collect(Collectors.toList());
    }

    @Override
    public List<Object> getFire(String address) {
        return List.of();
    }


    @Override
    public List<PersonInfoDTO> getPersonInfo(String lastName) {
        return List.of();
    }

    @Override
    public List<String> getCommunityEmail(String city) {
        return repository.getPersons().stream()
                .filter(p -> p.getCity().equalsIgnoreCase(city))
                .map(Person::getEmail)
                .filter(Objects::nonNull)
                .filter(s -> !s.isBlank())
                .distinct()
                .collect(Collectors.toList());
    }
}
