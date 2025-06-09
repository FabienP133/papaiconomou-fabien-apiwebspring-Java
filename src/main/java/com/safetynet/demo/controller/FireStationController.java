package com.safetynet.demo.controller;

import com.safetynet.demo.model.FireStation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/firestation")
public class FireStationController {

@PostMapping
public void createFireStation(FireStation fireStation) {
}



}
