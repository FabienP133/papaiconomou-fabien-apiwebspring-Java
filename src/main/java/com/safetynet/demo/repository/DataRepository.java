package com.safetynet.demo.repository;

import com.safetynet.demo.model.FireStation;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.demo.model.DataWrapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import jakarta.annotation.PostConstruct;

@Component
public class DataRepository {


    private DataWrapper data;
    private Path jsonPath;
    private final ObjectMapper mapper = new ObjectMapper();
    
    @PostConstruct
    public void init() {
        try {
            // charger data.json
            ClassPathResource res = new ClassPathResource("data.json");
            try (InputStream is = res.getInputStream()) {
                data = mapper.readValue(is, DataWrapper.class);
            }
            // garder le chemin du fichier pour pouvoir le réécrire
            File file = res.getFile();
            jsonPath = file.toPath();
        } catch (Exception e) {
            throw new RuntimeException("Impossible de charger data.json", e);
        }
    }

    //ajoute une nouvelle FireStation
    public FireStation addFirestation(FireStation fs) {
        data.getFirestations().add(fs);
        saveData();
        return fs;
    }

    //réécrit data.json avec l'état courant de `data`.
    public void saveData() {
        try {
            mapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValue(jsonPath.toFile(), data);
        } catch (Exception e) {
            throw new RuntimeException("Échec de la sauvegarde de data.json", e);
        }
    }


    public List<FireStation> getFirestations() {
        return data.getFirestations();
    }
}
