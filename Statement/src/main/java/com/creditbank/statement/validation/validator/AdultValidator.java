package com.creditbank.statement.validation.validator;

import com.creditbank.statement.validation.annotation.Adult;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class AdultValidator implements ConstraintValidator<Adult, LocalDate> {

    @Override
    public boolean isValid(LocalDate birthdate, ConstraintValidatorContext context) {
        if (birthdate == null) return true;

        return birthdate.isBefore(LocalDate.now().minusYears(18));
    }
}