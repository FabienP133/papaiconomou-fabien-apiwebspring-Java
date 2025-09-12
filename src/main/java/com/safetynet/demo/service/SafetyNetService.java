package com.safetynet.demo.service;

import com.safetynet.demo.dto.*;

import java.util.List;

public interface SafetyNetService {

    List<String> getPhoneAlert(int stationNumber);
    List<String> getCommunityEmail(String city);
    FireResponseDTO getFire(String address);
    List<PersonInfoDTO> getPersonInfo(String lastName);
    StationCoverageDTO getStationCoverage(int stationNumber);
    FloodStationsDTO getFloodStations(java.util.List<Integer> stations);
    List<ChildAlertChildDTO> getChildAlert(String address);

}
