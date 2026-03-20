package com.creditbank.calculator.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class AgeUtilTest {

    @Test
    void shouldReturnCorrectAgeWhenBirthdayAlreadyPassedThisYear() {
        LocalDate birthdate = LocalDate.now().minusYears(30).minusDays(1);

        int age = AgeUtil.calculateAge(birthdate);

        assertEquals(30, age);
    }

    @Test
    void shouldReturnCorrectAgeWhenBirthdayIsToday() {
        LocalDate birthdate = LocalDate.now().minusYears(25);

        int age = AgeUtil.calculateAge(birthdate);

        assertEquals(25, age);
    }

    @Test
    void shouldReturnCorrectAgeWhenBirthdayNotYetThisYear() {
        LocalDate birthdate = LocalDate.now().minusYears(40).plusDays(1);

        int age = AgeUtil.calculateAge(birthdate);

        assertEquals(39, age);
    }

    @Test
    void shouldReturnZeroAgeWhenBirthdateIsToday() {
        LocalDate birthdate = LocalDate.now();

        int age = AgeUtil.calculateAge(birthdate);

        assertEquals(0, age);
    }
}