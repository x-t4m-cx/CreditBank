package com.creditbank.deal.validation.validator;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class AdultValidatorTest {

    @Mock
    private ConstraintValidatorContext context;

    @InjectMocks
    private AdultValidator validator;

    @Test
    void shouldReturnTrueWhenBirthdateIsNull() {
        assertTrue(validator.isValid(null, context));
    }

    @Test
    void shouldReturnTrueWhenOlderThan18() {
        LocalDate birthdate = LocalDate.now().minusYears(19);
        assertTrue(validator.isValid(birthdate, context));
    }

    @Test
    void shouldReturnFalseWhenExactly18() {
        LocalDate birthdate = LocalDate.now().minusYears(18);
        assertFalse(validator.isValid(birthdate, context));
    }

    @Test
    void shouldReturnFalseWhenYoungerThan18() {
        LocalDate birthdate = LocalDate.now().minusYears(17);
        assertFalse(validator.isValid(birthdate, context));
    }
}

