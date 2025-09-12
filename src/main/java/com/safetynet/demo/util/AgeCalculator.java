package com.safetynet.demo.util;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public final class AgeCalculator {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    private AgeCalculator() {}

    public static int calculateAge(String birthdate) {
        LocalDate birth = LocalDate.parse(birthdate, FMT);
        return Period.between(birth, LocalDate.now()).getYears();
    }
}
