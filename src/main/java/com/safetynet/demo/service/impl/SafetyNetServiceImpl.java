package com.safetynet.demo.service.impl;

import com.safetynet.demo.dto.*;
import com.safetynet.demo.model.FireStation;
import com.safetynet.demo.model.MedicalRecord;
import com.safetynet.demo.model.Person;
import com.safetynet.demo.repository.DataRepository;
import com.safetynet.demo.service.SafetyNetService;
import com.safetynet.demo.util.AgeCalculator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class SafetyNetServiceImpl implements SafetyNetService {

    private final DataRepository repository;

    public SafetyNetServiceImpl(DataRepository repository) {
        this.repository = repository;
    }



    @Override
    public List<String> getPhoneAlert(int stationNumber) {
        String stationStr = String.valueOf(stationNumber);

        return repository.getFirestations().stream()
                .filter(fs -> stationStr.equals(fs.getStation()))
                .map(FireStation::getAddress)
                .flatMap(addr -> repository.getPersons().stream()
                        .filter(p -> addr.equalsIgnoreCase(p.getAddress()))
                        .map(Person::getPhone))
                .filter(Objects::nonNull)
                .filter(s -> !s.isBlank()).distinct().collect(Collectors.toList());
    }

    // helper réutilisé
    private FirePersonDTO toFirePersonDTO(Person p, MedicalRecord mr) {
        int age = AgeCalculator.calculateAge(mr.getBirthdate());
        return new FirePersonDTO(p.getFirstName(), p.getLastName(), p.getPhone(), age, mr.getMedications(), mr.getAllergies());
    }

    @Override
    public FireResponseDTO getFire(String address) {
        var stationOpt = repository.getFirestations().stream()
                .filter(fs -> address.equalsIgnoreCase(fs.getAddress()))
                .map(FireStation::getStation)
                .findFirst();
        if (stationOpt.isEmpty()) return null;

        String station = stationOpt.get();
        var people = repository.getPersons().stream()
                .filter(p -> address.equalsIgnoreCase(p.getAddress()))
                .toList();

        List<FirePersonDTO> persons = new ArrayList<>();
        for (Person p : people) {
            var mrOpt = repository.getMedicalRecords().stream()
                    .filter(m -> m.getFirstName().equalsIgnoreCase(p.getFirstName())
                            && m.getLastName().equalsIgnoreCase(p.getLastName()))
                    .findFirst();
            if (mrOpt.isEmpty()) continue;

            MedicalRecord mr = mrOpt.get();
            persons.add(toFirePersonDTO(p, mr));
        }
        return new FireResponseDTO(stationOpt.get(), persons);
    }


    @Override
    public List<PersonInfoDTO> getPersonInfo(String lastName) {
        return repository.getPersons().stream()
                .filter(p -> p.getLastName().equalsIgnoreCase(lastName))
                .flatMap (p -> {
                    var mrOpt = repository.getMedicalRecords().stream()
                            .filter(m -> m.getFirstName().equalsIgnoreCase(p.getFirstName())
                                    && m.getLastName().equalsIgnoreCase(p.getLastName()))
                            .findFirst();

                    if (mrOpt.isEmpty()) {
                        return Stream.empty();
                    } else {
                        MedicalRecord mr = mrOpt.get();
                        return Stream.of( new PersonInfoDTO(
                                        p.getFirstName(), p.getLastName(), p.getAddress(),
                                AgeCalculator.calculateAge(mr.getBirthdate()), p.getEmail(), mr.getMedications(), mr.getAllergies()
                                )
                        );
                    }

                })
                .collect(Collectors.toList());
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


    @Override
    public StationCoverageDTO getStationCoverage(int stationNumber) {
        String stationStr = Integer.toString(stationNumber);

        // adresses couvertes par la station
        var addresses = repository.getFirestations().stream()
                .filter(fs -> stationStr.equals(fs.getStation()))
                .map(FireStation::getAddress)
                .collect(Collectors.toSet());

        // station inconnue -> laisser le contrôleur renvoyer {}
        if (addresses.isEmpty()) return null;

        // personnes habitant ces adresses
        var covered = repository.getPersons().stream()
                .filter(p -> addresses.contains(p.getAddress()))
                .toList();

        // construire la liste persons (DTO minimal) + compter enfants/adultes
        var persons = new ArrayList<StationCoveragePersonDTO>(covered.size());
        int adults = 0;
        int children = 0;

        for (Person p : covered) {
            persons.add(new StationCoveragePersonDTO(
                    p.getFirstName(), p.getLastName(), p.getAddress(), p.getPhone()
            ));

            // âge si dossier dispo ; enfant < 18, adulte >= 18 ; inconnu = non compté
            var mrOpt = repository.getMedicalRecords().stream()
                    .filter(m -> m.getFirstName().equalsIgnoreCase(p.getFirstName())
                            && m.getLastName().equalsIgnoreCase(p.getLastName()))
                    .findFirst();

            if (mrOpt.isPresent()) {
                int age = AgeCalculator.calculateAge(mrOpt.get().getBirthdate());
                if (age < 18) children++; else adults++;
            }
        }

        return new StationCoverageDTO(persons, children, adults);
    }

    @Override
    public List<ChildAlertChildDTO> getChildAlert(String address) {
        var household = repository.getPersons().stream()
                .filter(p -> address.equalsIgnoreCase(p.getAddress()))
                .toList();

        List<ChildAlertChildDTO> out = new ArrayList<>();
        for (Person p : household) {
            var mrOpt = repository.getMedicalRecords().stream()
                    .filter(m -> m.getFirstName().equalsIgnoreCase(p.getFirstName())
                            && m.getLastName().equalsIgnoreCase(p.getLastName()))
                    .findFirst();
            if (mrOpt.isEmpty()) continue;

            int age = AgeCalculator.calculateAge(mrOpt.get().getBirthdate());
            if (age < 18) {
                var members = household.stream()
                        .filter(o -> !(o.getFirstName().equalsIgnoreCase(p.getFirstName())
                                && o.getLastName().equalsIgnoreCase(p.getLastName())))
                        .map(o -> new ChildAlertHouseholdMemberDTO(o.getFirstName(), o.getLastName()))
                        .collect(Collectors.toList());

                out.add(new ChildAlertChildDTO(p.getFirstName(), p.getLastName(), age, members));
            }
        }
        return out;
    }

    @Override
    public FloodStationsDTO getFloodStations(List<Integer> stations) {
        Set<String> stationSet = stations.stream().map(String::valueOf).collect(Collectors.toSet());
        Set<String> addresses = repository.getFirestations().stream()
                .filter(fs -> stationSet.contains(fs.getStation()))
                .map(FireStation::getAddress)
                .collect(Collectors.toSet());

        List<FloodHouseholdDTO> households = new ArrayList<>();
        for (String addr : addresses) {
            var occupants = repository.getPersons().stream()
                    .filter(p -> addr.equalsIgnoreCase(p.getAddress()))
                    .toList();

            List<FirePersonDTO> persons = new ArrayList<>();
            for (Person p : occupants) {
                var mrOpt = repository.getMedicalRecords().stream()
                        .filter(m -> m.getFirstName().equalsIgnoreCase(p.getFirstName())
                                && m.getLastName().equalsIgnoreCase(p.getLastName()))
                        .findFirst();
                if (mrOpt.isEmpty()) continue;

                MedicalRecord mr = mrOpt.get();
                persons.add(toFirePersonDTO(p, mr));
            }
            households.add(new FloodHouseholdDTO(addr, persons));
        }
        return new FloodStationsDTO(households);
    }

}
