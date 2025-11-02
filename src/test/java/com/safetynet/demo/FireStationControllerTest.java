package com.safetynet.demo;


import com.safetynet.demo.controller.FireStationController;
import com.safetynet.demo.model.FireStation;
import com.safetynet.demo.service.FireStationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FireStationController.class)
public class FireStationControllerTest {

    @Autowired MockMvc mockMvc;

    @MockitoBean
    FireStationService service;

    @Autowired FireStationController controller;


    @Test
    void createFireStation_201() throws Exception {
        when(service.create(any(FireStation.class)))
                .thenReturn(new FireStation("A1", "2"));

        mockMvc.perform(post("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"address\":\"A1\",\"station\":\"2\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.address").value("A1"))
                .andExpect(jsonPath("$.station").value("2"));
    }

    @Test
    void createFireStation_conflict_409() throws Exception {
        when(service.create(any(FireStation.class)))
                .thenThrow(new com.safetynet.demo.exception.ConflictException("dup"));

        mockMvc.perform(post("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"address\":\"A1\",\"station\":\"2\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void updateFireStation_200() throws Exception {
        when(service.update(any(FireStation.class)))
                .thenReturn(new FireStation("A1", "3"));

        mockMvc.perform(put("/firestation/A1/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"address\":\"A1\",\"station\":\"3\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value("A1"))
                .andExpect(jsonPath("$.station").value("3"));
    }

    @Test
    void updateFireStation_notFound_404() throws Exception {
        when(service.update(any(FireStation.class)))
                .thenThrow(new com.safetynet.demo.exception.NotFoundException("missing"));

        mockMvc.perform(put("/firestation/A1/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"address\":\"A1\",\"station\":\"3\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteFireStation_204() throws Exception {
        doNothing().when(service).delete("A1", 3);
        mockMvc.perform(delete("/firestation/{address}/{station}", "A1", 3))
                .andExpect(status().isNoContent());
        org.mockito.Mockito.verify(service).delete("A1", 3);
    }

    @Test
    void deleteFireStation_notFound_404() throws Exception {
        org.mockito.Mockito.doThrow(new com.safetynet.demo.exception.NotFoundException("missing"))
                .when(service).delete("A1", 3);
        mockMvc.perform(delete("/firestation/{address}/{station}", "A1", 3))
                .andExpect(status().isNotFound());
        org.mockito.Mockito.verify(service).delete("A1", 3);
    }



}