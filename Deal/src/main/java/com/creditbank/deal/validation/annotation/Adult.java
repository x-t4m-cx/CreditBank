package com.creditbank.deal.validation.annotation;

import com.creditbank.deal.validation.validator.AdultValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AdultValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Adult {

    String message() default "Birthdate must be at least 18 years in the past";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
