package com.safetynet.demo;

import com.safetynet.demo.controller.AlertsController;
import com.safetynet.demo.dto.*;
import com.safetynet.demo.service.SafetyNetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AlertsController.class)
class AlertsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SafetyNetService service;

    // ---- /childAlert ----
    @Test
    void childAlert_returnsChildrenWithHousehold() throws Exception {
        var members = List.of(new ChildAlertHouseholdMemberDTO("Anna", "Smith"),
                new ChildAlertHouseholdMemberDTO("Bob", "Smith"));
        var child   = new ChildAlertChildDTO("Kid", "Smith", 10, members);

        when(service.getChildAlert("A1")).thenReturn(List.of(child));

        mockMvc.perform(get("/childAlert").param("address", "A1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName").value("Kid"))
                .andExpect(jsonPath("$[0].age").value(10))
                .andExpect(jsonPath("$[0].householdMembers", hasSize(2)))
                .andExpect(jsonPath("$[0].householdMembers[0].firstName").value("Anna"));
    }

    @Test
    void childAlert_noChildren_returnsEmptyArray() throws Exception {
        when(service.getChildAlert("A2")).thenReturn(List.of());
        mockMvc.perform(get("/childAlert").param("address", "A2"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    // ---- /fire ----
    @Test
    void fire_returnsStationAndPersons() throws Exception {
        var p1 = new FirePersonDTO("John", "Boyd", "841-874-6512", 41,
                List.of("aznol:200mg"), List.of("nillacilan"));
        var p2 = new FirePersonDTO("Jacob", "Boyd", "841-874-6513", 32,
                List.of(), List.of());
        var dto = new FireResponseDTO("3", List.of(p1, p2));

        when(service.getFire("1509 Culver St")).thenReturn(dto);

        mockMvc.perform(get("/fire").param("address", "1509 Culver St"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.station").value("3"))
                .andExpect(jsonPath("$.persons", hasSize(2)))
                .andExpect(jsonPath("$.persons[0].firstName").value("John"))
                .andExpect(jsonPath("$.persons[0].medications[0]").value("aznol:200mg"));
    }

    @Test
    void fire_unknownAddress_returns404() throws Exception {
        when(service.getFire("Unknown")).thenReturn(null);
        mockMvc.perform(get("/fire").param("address", "Unknown"))
                .andExpect(status().isNotFound());
    }

    // ---- /flood/stations ----
    @Test
    void floodStations_returnsHouseholdsList() throws Exception {
        var p1 = new FirePersonDTO("P1", "L1", "111", 20, List.of("m1"), List.of("a1"));
        var p2 = new FirePersonDTO("P2", "L2", "222", 10, List.of(), List.of());

        var h1 = new FloodHouseholdDTO("A1", List.of(p1));
        var h2 = new FloodHouseholdDTO("A2", List.of(p2));
        var dto = new FloodStationsDTO(List.of(h1, h2));

        when(service.getFloodStations(List.of(1, 2))).thenReturn(dto);

        mockMvc.perform(get("/flood/stations").param("stations", "1,2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.households", hasSize(2)))
                .andExpect(jsonPath("$.households[0].address", anyOf(is("A1"), is("A2"))))
                .andExpect(jsonPath("$.households[0].persons[0].firstName", anyOf(is("P1"), is("P2"))));
    }

    // -------- stationCoverage --------
    @Test
    void stationCoverage_200() throws Exception {
        var persons = List.of(
                new StationCoveragePersonDTO("John","Boyd","1509 Culver St","111-111"),
                new StationCoveragePersonDTO("Jacob","Boyd","1509 Culver St","222-222")
        );
        var dto = new StationCoverageDTO(persons, /*children*/1, /*adults*/1);
        when(service.getStationCoverage(3)).thenReturn(dto);

        mockMvc.perform(get("/firestation").param("stationNumber", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.children").value(1))
                .andExpect(jsonPath("$.adults").value(1))
                .andExpect(jsonPath("$.persons[0].firstName").value("John"))
                .andExpect(jsonPath("$.persons[0].phone").value("111-111"));
    }

    // -------- phoneAlert --------
    @Test
    void phoneAlert_200() throws Exception {
        when(service.getPhoneAlert(1))
                .thenReturn(java.util.List.of("111-111", "222-222"));

        mockMvc.perform(get("/phoneAlert").param("firestation", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("111-111"));
    }

    // -------- communityEmail --------
    @Test
    void communityEmail_200() throws Exception {
        when(service.getCommunityEmail("Culver"))
                .thenReturn(java.util.List.of("a@x.com", "b@x.com"));

        mockMvc.perform(get("/communityEmail").param("city", "Culver"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("a@x.com"));
    }

    // -------- personInfoLastName --------
    @Test
    void personInfoLastName_200() throws Exception {
        var list = List.of(
                new PersonInfoDTO("John", "Boyd", "1509 Culver St", 40, "john@x",
                        List.of("aznol:200mg"), List.of("nillacilan"))
        );
        when(service.getPersonInfo("Boyd")).thenReturn(List.of(
                new PersonInfoDTO("John", "Boyd", "1509 Culver St", 40, "john@x",
                        List.of("aznol:200mg"), List.of("nillacilan"))
        ));

        mockMvc.perform(get("/personInfo")                 // ← au lieu de /personInfoLastName
                        .param("lastName", "Boyd"))               // ← param s’appelle bien lastName
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].age").value(40))
                .andExpect(jsonPath("$[0].medications[0]").value("aznol:200mg"));
    }

}