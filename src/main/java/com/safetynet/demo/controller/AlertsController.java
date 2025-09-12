package com.safetynet.demo.controller;

import com.safetynet.demo.dto.*;
import com.safetynet.demo.service.SafetyNetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
public class AlertsController {

    private final SafetyNetService service;

    public AlertsController(SafetyNetService service) {
        this.service = service;
    }


    @GetMapping("/phoneAlert")
    public List<String> phoneAlert(@RequestParam("firestation") int station) {
        return service.getPhoneAlert(station);
    }

    @GetMapping("/communityEmail")
    public List<String> communityEmail(@RequestParam String city) {
        return service.getCommunityEmail(city);
    }


    @GetMapping("/personInfo")
    public List<PersonInfoDTO> personInfo(@RequestParam String lastName) {
        return service.getPersonInfo(lastName);
    }

    @GetMapping(value = "/firestation", params = "stationNumber")
    public ResponseEntity<?> stationCoverage(@RequestParam int stationNumber) {
        StationCoverageDTO dto = service.getStationCoverage(stationNumber);
        return (dto == null) ? ResponseEntity.ok(Collections.emptyMap()) : ResponseEntity.ok(dto);
    }


    // DTOs pour les endpoints manquants
    @GetMapping("/childAlert")
    public List<ChildAlertChildDTO> childAlert(@RequestParam String address) {
        return service.getChildAlert(address);
    }

    @GetMapping("/fire")
    public ResponseEntity<?> fire(@RequestParam String address) {
        FireResponseDTO dto = service.getFire(address);
        return (dto == null) ? ResponseEntity.ok(Collections.emptyMap()) : ResponseEntity.ok(dto);
    }

    @GetMapping("/flood/stations")
    public FloodStationsDTO floodStations(@RequestParam List<Integer> stations) {
        return service.getFloodStations(stations);
    }

}
