package com.creditbank.statement.validation.validator;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class AdultValidatorTest {

    private final AdultValidator validator = new AdultValidator();

    @Test
    void shouldReturnTrueWhenBirthdateIsNull() {
        assertThat(validator.isValid(null, null)).isTrue();
    }

    @Test
    void shouldReturnFalseWhenUserIsUnder18() {
        LocalDate birthdate = LocalDate.now().minusYears(18).plusDays(1);
        assertThat(validator.isValid(birthdate, null)).isFalse();
    }

    @Test
    void shouldReturnTrueWhenUserIsOver18() {
        LocalDate birthdate = LocalDate.now().minusYears(18).minusDays(1);
        assertThat(validator.isValid(birthdate, null)).isTrue();
    }
}

