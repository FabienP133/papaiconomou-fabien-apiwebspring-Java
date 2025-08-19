package com.safetynet.demo.service;

import com.safetynet.demo.dto.PersonInfoDTO;

import java.util.List;

public interface SafetyNetService {

    List<String> getPhoneAlert(int stationNumber);
    List<String> getCommunityEmail(String city);
    List<Object> getFire(String address);
    List<PersonInfoDTO> getPersonInfo(String lastName);

}
