package com.safetynet.demo.controller;

import com.safetynet.demo.dto.*;
import com.safetynet.demo.service.SafetyNetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AlertsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlertsController.class);

    private final SafetyNetService service;

    public AlertsController(SafetyNetService service) {
        this.service = service;
    }


    @GetMapping("/phoneAlert")
    public List<String> phoneAlert(@RequestParam("firestation") int station) {
         LOGGER.info("phoneAlert appelé " + station);
        return service.getPhoneAlert(station);
    }

    @GetMapping("/communityEmail")
    public List<String> communityEmail(@RequestParam String city) {
        LOGGER.info("communityEmail appelé " + city);
        return service.getCommunityEmail(city);
    }


    @GetMapping("/personInfo")
    public List<PersonInfoDTO> personInfoLastName(@RequestParam String lastName) {
        LOGGER.info("personInfoLastName appelé " + lastName);
        return service.getPersonInfo(lastName);
    }

    @GetMapping(value = "/firestation", params = "stationNumber")
    public ResponseEntity<StationCoverageDTO> stationCoverage(@RequestParam int stationNumber) {
        LOGGER.info("stationCoverage appelé " + stationNumber);
        StationCoverageDTO dto = service.getStationCoverage(stationNumber);
        return (dto == null) ? ResponseEntity.notFound().build() : ResponseEntity.ok(dto);
    }


    // DTOs pour les endpoints manquants
    @GetMapping("/childAlert")
    public List<ChildAlertChildDTO> childAlert(@RequestParam String address) {
        LOGGER.info("childAlert appelé " + address);
        return service.getChildAlert(address);
    }

    @GetMapping("/fire")
    public ResponseEntity<FireResponseDTO> fire(@RequestParam String address) {
        LOGGER.info("fire appelé " + address);
        FireResponseDTO dto = service.getFire(address);
        return (dto == null) ? ResponseEntity.notFound().build() : ResponseEntity.ok(dto);
    }

    @GetMapping("/flood/stations")
    public FloodStationsDTO floodStations(@RequestParam List<Integer> stations) {
        LOGGER.info("floodStations appelé " + stations);
        return service.getFloodStations(stations);
    }

}
