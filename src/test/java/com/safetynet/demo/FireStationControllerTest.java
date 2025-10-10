package com.safetynet.demo;


import com.safetynet.demo.controller.FireStationController;
import com.safetynet.demo.model.FireStation;
import com.safetynet.demo.repository.DataRepository;
import com.safetynet.demo.service.FireStationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FireStationController.class)
public class FireStationControllerTest {

    @Autowired MockMvc mockMvc;

    // Beans dont dépend le controller
    @MockitoBean
    DataRepository repository;
    @MockitoBean FireStationService fireStationService;

    // On récupère l’instance réelle du controller pour y injecter le mock FireStationService (champ privé)
    @Autowired FireStationController controller;

    @BeforeEach
    void wireMissingService() {
        // Injection manuelle du mock dans le champ privé non-injecté par le constructeur
        ReflectionTestUtils.setField(controller, "fireStationService", fireStationService);
    }

    @Test
    void createFireStation_201() throws Exception {
        var list = new ArrayList<FireStation>();
        when(repository.getFirestations()).thenReturn(list);

        mockMvc.perform(post("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"address\":\"A1\",\"station\":\"2\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.address").value("A1"))
                .andExpect(jsonPath("$.station").value("2"));
    }

    @Test
    void updateFireStation_200() throws Exception {
        var list = new ArrayList<FireStation>();
        list.add(new FireStation("A1","2"));
        when(repository.getFirestations()).thenReturn(list);

        mockMvc.perform(put("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"address\":\"A1\",\"station\":\"3\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value("A1"))
                .andExpect(jsonPath("$.station").value("3"));
    }

    @Test
    void deleteFireStation_200() throws Exception {
        // La méthode delete du service est void → on neutralise
        doNothing().when(fireStationService).delete(eq("A1"), eq(3));

        mockMvc.perform(delete("/firestation")
                        .param("address", "A1")
                        .param("station", "3"))
                .andExpect(status().isOk());
    }

}