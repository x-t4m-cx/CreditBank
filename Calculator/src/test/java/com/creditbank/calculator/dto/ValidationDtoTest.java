package com.creditbank.calculator.dto;

import com.creditbank.calculator.dto.request.EmploymentDto;
import com.creditbank.calculator.dto.request.LoanStatementRequestDto;
import com.creditbank.calculator.dto.request.ScoringDataDto;
import com.creditbank.calculator.enums.EmploymentStatus;
import com.creditbank.calculator.enums.Gender;
import com.creditbank.calculator.enums.MaritalStatus;
import com.creditbank.calculator.enums.Position;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidationDtoTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void loanStatementRequestDtoShouldHaveViolationsWhenInvalid() {
        LoanStatementRequestDto dto = LoanStatementRequestDto.builder()
                .amount(new BigDecimal("1000")) // < 20000
                .term(1) // < 6
                .firstName("I") // too short
                .lastName("P") // too short
                .email("not-an-email")
                .birthdate(LocalDate.now().plusDays(1)) // not past
                .passportSeries("12")
                .passportNumber("123")
                .build();

        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    void scoringDataDtoShouldBeValidWhenAllFieldsProvidedCorrectly() {
        ScoringDataDto dto = ScoringDataDto.builder()
                .amount(new BigDecimal("20000.00"))
                .term(6)
                .firstName("Ivan")
                .lastName("Petrov")
                .middleName("Sergeevich")
                .gender(Gender.MALE)
                .birthdate(LocalDate.now().minusYears(30))
                .passportSeries("1234")
                .passportNumber("567890")
                .passportIssueDate(LocalDate.now().minusYears(5))
                .passportIssueBranch("MVD Russia 770-001")
                .maritalStatus(MaritalStatus.SINGLE)
                .dependentAmount(0)
                .employment(EmploymentDto.builder()
                        .employmentStatus(EmploymentStatus.EMPLOYED)
                        .employerINN("7707083893")
                        .salary(new BigDecimal("100000.00"))
                        .position(Position.WORKER)
                        .workExperienceTotal(120)
                        .workExperienceCurrent(24)
                        .build())
                .accountNumber("40817810099910004312")
                .isInsuranceEnabled(false)
                .isSalaryClient(true)
                .build();

        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    void employmentDtoShouldHaveViolationsWhenInvalid() {
        EmploymentDto dto = EmploymentDto.builder()
                .employmentStatus(null)
                .employerINN("1")
                .salary(new BigDecimal("0"))
                .position(null)
                .workExperienceTotal(-1)
                .workExperienceCurrent(-1)
                .build();

        assertFalse(validator.validate(dto).isEmpty());
    }
}

