package com.safetynet.demo.controller;

import com.safetynet.demo.service.SafetyNetService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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


}
