package com.creditbank.calculator.util;

import java.time.LocalDate;
import java.time.Period;

public final class AgeUtil {
    private AgeUtil() {}
    public static int calculateAge(LocalDate birthdate) {
        return Period.between(birthdate, LocalDate.now()).getYears();
    }
}