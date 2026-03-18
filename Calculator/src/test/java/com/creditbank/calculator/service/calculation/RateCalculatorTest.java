package com.creditbank.calculator.service.calculation;

import com.creditbank.calculator.config.property.ScoringProperties;
import com.creditbank.calculator.dto.request.EmploymentDto;
import com.creditbank.calculator.dto.request.ScoringDataDto;
import com.creditbank.calculator.enums.EmploymentStatus;
import com.creditbank.calculator.enums.Gender;
import com.creditbank.calculator.enums.MaritalStatus;
import com.creditbank.calculator.enums.Position;
import com.creditbank.calculator.exception.ScoringException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RateCalculatorTest {

    @Test
    void shouldCalculatePrescoringRateWithAllDiscountsApplied() {
        ScoringProperties props = new ScoringProperties();
        props.setBaseRate(new BigDecimal("15.0"));
        props.setInsuranceDecrease(new BigDecimal("3.0"));
        props.setSalaryClientDecrease(new BigDecimal("1.0"));

        RateCalculator calculator = new RateCalculator(props);

        BigDecimal rate = calculator.calculatePrescoringRate(true, true);

        assertEquals(new BigDecimal("11.0"), rate);
    }

    @Test
    void shouldApplyAllScoringModifiersWhenEligible() {
        ScoringProperties props = new ScoringProperties();
        props.setBaseRate(new BigDecimal("15.0"));
        props.setInsuranceDecrease(new BigDecimal("3.0"));
        props.setSalaryClientDecrease(new BigDecimal("1.0"));
        props.setSelfEmployedIncrease(new BigDecimal("2.0"));
        props.setMidManagerDecrease(new BigDecimal("2.0"));
        props.setMarriedDecrease(new BigDecimal("3.0"));
        props.setWomanAgeDecrease(new BigDecimal("3.0"));

        RateCalculator calculator = new RateCalculator(props);

        ScoringDataDto dto = ScoringDataDto.builder()
                .isInsuranceEnabled(true)
                .isSalaryClient(true)
                .employment(EmploymentDto.builder()
                        .employmentStatus(EmploymentStatus.SELF_EMPLOYED)
                        .position(Position.MID_MANAGER)
                        .salary(new BigDecimal("100000"))
                        .workExperienceTotal(120)
                        .workExperienceCurrent(24)
                        .employerINN("7707083893")
                        .build())
                .maritalStatus(MaritalStatus.MARRIED)
                .gender(Gender.FEMALE)
                .birthdate(LocalDate.now().minusYears(40))
                .build();

        BigDecimal rate = calculator.calculateScoringRate(dto);

        // base 15 -3 -1 +2 -2 -3 -3 = 5
        assertEquals(new BigDecimal("5.0"), rate);
    }

    @Test
    void shouldApplyNonBinaryIncrease() {
        ScoringProperties props = new ScoringProperties();
        props.setBaseRate(new BigDecimal("10.0"));
        props.setNonBinaryIncrease(new BigDecimal("7.0"));

        RateCalculator calculator = new RateCalculator(props);

        ScoringDataDto dto = ScoringDataDto.builder()
                .isInsuranceEnabled(false)
                .isSalaryClient(false)
                .employment(EmploymentDto.builder()
                        .employmentStatus(EmploymentStatus.EMPLOYED)
                        .position(Position.WORKER)
                        .salary(new BigDecimal("100000"))
                        .workExperienceTotal(120)
                        .workExperienceCurrent(24)
                        .employerINN("7707083893")
                        .build())
                .maritalStatus(MaritalStatus.SINGLE)
                .gender(Gender.NON_BINARY)
                .birthdate(LocalDate.now().minusYears(25))
                .build();

        BigDecimal rate = calculator.calculateScoringRate(dto);

        assertEquals(new BigDecimal("17.0"), rate);
    }

    @Test
    void shouldCalculatePrescoringRateWithoutDiscounts() {
        ScoringProperties props = new ScoringProperties();
        props.setBaseRate(new BigDecimal("15.0"));

        RateCalculator calculator = new RateCalculator(props);

        BigDecimal rate = calculator.calculatePrescoringRate(false, false);

        assertEquals(new BigDecimal("15.0"), rate);
    }

    @Test
    void shouldCalculatePrescoringRateWithOnlyInsuranceDiscount() {
        ScoringProperties props = new ScoringProperties();
        props.setBaseRate(new BigDecimal("15.0"));
        props.setInsuranceDecrease(new BigDecimal("3.0"));

        RateCalculator calculator = new RateCalculator(props);

        BigDecimal rate = calculator.calculatePrescoringRate(true, false);

        assertEquals(new BigDecimal("12.0"), rate);
    }

    @Test
    void shouldCalculatePrescoringRateWithOnlySalaryClientDiscount() {
        ScoringProperties props = new ScoringProperties();
        props.setBaseRate(new BigDecimal("15.0"));
        props.setSalaryClientDecrease(new BigDecimal("1.0"));

        RateCalculator calculator = new RateCalculator(props);

        BigDecimal rate = calculator.calculatePrescoringRate(false, true);

        assertEquals(new BigDecimal("14.0"), rate);
    }

    @Test
    void shouldApplyBusinessOwnerIncrease() {
        ScoringProperties props = new ScoringProperties();
        props.setBaseRate(new BigDecimal("10.0"));
        props.setBusinessOwnerIncrease(new BigDecimal("1.0"));

        RateCalculator calculator = new RateCalculator(props);

        ScoringDataDto dto = ScoringDataDto.builder()
                .isInsuranceEnabled(false)
                .isSalaryClient(false)
                .employment(EmploymentDto.builder()
                        .employmentStatus(EmploymentStatus.BUSINESS_OWNER)
                        .position(Position.WORKER)
                        .salary(new BigDecimal("100000"))
                        .workExperienceTotal(120)
                        .workExperienceCurrent(24)
                        .employerINN("7707083893")
                        .build())
                .maritalStatus(MaritalStatus.SINGLE)
                .gender(Gender.MALE)
                .birthdate(LocalDate.now().minusYears(25))
                .build();

        BigDecimal rate = calculator.calculateScoringRate(dto);

        assertEquals(new BigDecimal("11.0"), rate);
    }

    @Test
    void shouldApplyNoEmploymentModifierWhenEmployed() {
        ScoringProperties props = new ScoringProperties();
        props.setBaseRate(new BigDecimal("10.0"));

        RateCalculator calculator = new RateCalculator(props);

        ScoringDataDto dto = ScoringDataDto.builder()
                .isInsuranceEnabled(false)
                .isSalaryClient(false)
                .employment(EmploymentDto.builder()
                        .employmentStatus(EmploymentStatus.EMPLOYED)
                        .position(Position.WORKER)
                        .salary(new BigDecimal("100000"))
                        .workExperienceTotal(120)
                        .workExperienceCurrent(24)
                        .employerINN("7707083893")
                        .build())
                .maritalStatus(MaritalStatus.SINGLE)
                .gender(Gender.MALE)
                .birthdate(LocalDate.now().minusYears(25))
                .build();

        BigDecimal rate = calculator.calculateScoringRate(dto);

        assertEquals(new BigDecimal("10.0"), rate);
    }

    @Test
    void shouldApplyTopManagerDecrease() {
        ScoringProperties props = new ScoringProperties();
        props.setBaseRate(new BigDecimal("12.0"));
        props.setTopManagerDecrease(new BigDecimal("3.0"));

        RateCalculator calculator = new RateCalculator(props);

        ScoringDataDto dto = ScoringDataDto.builder()
                .isInsuranceEnabled(false)
                .isSalaryClient(false)
                .employment(EmploymentDto.builder()
                        .employmentStatus(EmploymentStatus.EMPLOYED)
                        .position(Position.TOP_MANAGER)
                        .salary(new BigDecimal("100000"))
                        .workExperienceTotal(120)
                        .workExperienceCurrent(24)
                        .employerINN("7707083893")
                        .build())
                .maritalStatus(MaritalStatus.SINGLE)
                .gender(Gender.MALE)
                .birthdate(LocalDate.now().minusYears(25))
                .build();

        BigDecimal rate = calculator.calculateScoringRate(dto);

        assertEquals(new BigDecimal("9.0"), rate);
    }

    @Test
    void shouldApplyNoPositionModifierWhenWorker() {
        ScoringProperties props = new ScoringProperties();
        props.setBaseRate(new BigDecimal("10.0"));

        RateCalculator calculator = new RateCalculator(props);

        ScoringDataDto dto = ScoringDataDto.builder()
                .isInsuranceEnabled(false)
                .isSalaryClient(false)
                .employment(EmploymentDto.builder()
                        .employmentStatus(EmploymentStatus.EMPLOYED)
                        .position(Position.WORKER)
                        .salary(new BigDecimal("100000"))
                        .workExperienceTotal(120)
                        .workExperienceCurrent(24)
                        .employerINN("7707083893")
                        .build())
                .maritalStatus(MaritalStatus.SINGLE)
                .gender(Gender.MALE)
                .birthdate(LocalDate.now().minusYears(25))
                .build();

        BigDecimal rate = calculator.calculateScoringRate(dto);

        assertEquals(new BigDecimal("10.0"), rate);
    }

    @Test
    void shouldApplyDivorcedIncrease() {
        ScoringProperties props = new ScoringProperties();
        props.setBaseRate(new BigDecimal("10.0"));
        props.setDivorcedIncrease(new BigDecimal("1.0"));

        RateCalculator calculator = new RateCalculator(props);

        ScoringDataDto dto = ScoringDataDto.builder()
                .isInsuranceEnabled(false)
                .isSalaryClient(false)
                .employment(EmploymentDto.builder()
                        .employmentStatus(EmploymentStatus.EMPLOYED)
                        .position(Position.WORKER)
                        .salary(new BigDecimal("100000"))
                        .workExperienceTotal(120)
                        .workExperienceCurrent(24)
                        .employerINN("7707083893")
                        .build())
                .maritalStatus(MaritalStatus.DIVORCED)
                .gender(Gender.MALE)
                .birthdate(LocalDate.now().minusYears(25))
                .build();

        BigDecimal rate = calculator.calculateScoringRate(dto);

        assertEquals(new BigDecimal("11.0"), rate);
    }

    @Test
    void shouldApplyNoMaritalModifierWhenSingle() {
        ScoringProperties props = new ScoringProperties();
        props.setBaseRate(new BigDecimal("10.0"));

        RateCalculator calculator = new RateCalculator(props);

        ScoringDataDto dto = ScoringDataDto.builder()
                .isInsuranceEnabled(false)
                .isSalaryClient(false)
                .employment(EmploymentDto.builder()
                        .employmentStatus(EmploymentStatus.EMPLOYED)
                        .position(Position.WORKER)
                        .salary(new BigDecimal("100000"))
                        .workExperienceTotal(120)
                        .workExperienceCurrent(24)
                        .employerINN("7707083893")
                        .build())
                .maritalStatus(MaritalStatus.SINGLE)
                .gender(Gender.MALE)
                .birthdate(LocalDate.now().minusYears(25))
                .build();

        BigDecimal rate = calculator.calculateScoringRate(dto);

        assertEquals(new BigDecimal("10.0"), rate);
    }

    @Test
    void shouldApplyMaleAgeDecreaseWhenAgeInRange() {
        ScoringProperties props = new ScoringProperties();
        props.setBaseRate(new BigDecimal("12.0"));
        props.setManAgeDecrease(new BigDecimal("3.0"));

        RateCalculator calculator = new RateCalculator(props);

        ScoringDataDto dto = ScoringDataDto.builder()
                .isInsuranceEnabled(false)
                .isSalaryClient(false)
                .employment(EmploymentDto.builder()
                        .employmentStatus(EmploymentStatus.EMPLOYED)
                        .position(Position.WORKER)
                        .salary(new BigDecimal("100000"))
                        .workExperienceTotal(120)
                        .workExperienceCurrent(24)
                        .employerINN("7707083893")
                        .build())
                .maritalStatus(MaritalStatus.SINGLE)
                .gender(Gender.MALE)
                .birthdate(LocalDate.now().minusYears(35))
                .build();

        BigDecimal rate = calculator.calculateScoringRate(dto);

        assertEquals(new BigDecimal("9.0"), rate);
    }
    @Test
    void shouldApplyMaleAgeDecreaseAtLowerBound() {
        ScoringProperties props = new ScoringProperties();
        props.setBaseRate(new BigDecimal("12.0"));
        props.setManAgeDecrease(new BigDecimal("3.0"));

        RateCalculator calculator = new RateCalculator(props);

        ScoringDataDto dto = ScoringDataDto.builder()
                .isInsuranceEnabled(false)
                .isSalaryClient(false)
                .employment(EmploymentDto.builder()
                        .employmentStatus(EmploymentStatus.EMPLOYED)
                        .position(Position.WORKER)
                        .salary(new BigDecimal("100000"))
                        .workExperienceTotal(120)
                        .workExperienceCurrent(24)
                        .employerINN("7707083893")
                        .build())
                .maritalStatus(MaritalStatus.SINGLE)
                .gender(Gender.MALE)
                .birthdate(LocalDate.now().minusYears(30))
                .build();

        BigDecimal rate = calculator.calculateScoringRate(dto);

        assertEquals(new BigDecimal("9.0"), rate);
    }

    @Test
    void shouldApplyMaleAgeDecreaseAtUpperBound() {
        ScoringProperties props = new ScoringProperties();
        props.setBaseRate(new BigDecimal("12.0"));
        props.setManAgeDecrease(new BigDecimal("3.0"));

        RateCalculator calculator = new RateCalculator(props);

        ScoringDataDto dto = ScoringDataDto.builder()
                .isInsuranceEnabled(false)
                .isSalaryClient(false)
                .employment(EmploymentDto.builder()
                        .employmentStatus(EmploymentStatus.EMPLOYED)
                        .position(Position.WORKER)
                        .salary(new BigDecimal("100000"))
                        .workExperienceTotal(120)
                        .workExperienceCurrent(24)
                        .employerINN("7707083893")
                        .build())
                .maritalStatus(MaritalStatus.SINGLE)
                .gender(Gender.MALE)
                .birthdate(LocalDate.now().minusYears(55))
                .build();

        BigDecimal rate = calculator.calculateScoringRate(dto);

        assertEquals(new BigDecimal("9.0"), rate);
    }
    @Test
    void shouldNotApplyFemaleAgeDecreaseWhenAgeBelowRange() {
        ScoringProperties props = new ScoringProperties();
        props.setBaseRate(new BigDecimal("10.0"));
        props.setWomanAgeDecrease(new BigDecimal("3.0"));

        RateCalculator calculator = new RateCalculator(props);

        ScoringDataDto dto = ScoringDataDto.builder()
                .isInsuranceEnabled(false)
                .isSalaryClient(false)
                .employment(EmploymentDto.builder()
                        .employmentStatus(EmploymentStatus.EMPLOYED)
                        .position(Position.WORKER)
                        .salary(new BigDecimal("100000"))
                        .workExperienceTotal(120)
                        .workExperienceCurrent(24)
                        .employerINN("7707083893")
                        .build())
                .maritalStatus(MaritalStatus.SINGLE)
                .gender(Gender.FEMALE)
                .birthdate(LocalDate.now().minusYears(31))
                .build();

        BigDecimal rate = calculator.calculateScoringRate(dto);

        assertEquals(new BigDecimal("10.0"), rate);
    }

    @Test
    void shouldNotApplyMaleAgeDecreaseWhenAgeBelowRange() {
        ScoringProperties props = new ScoringProperties();
        props.setBaseRate(new BigDecimal("10.0"));
        props.setManAgeDecrease(new BigDecimal("3.0"));

        RateCalculator calculator = new RateCalculator(props);

        ScoringDataDto dto = ScoringDataDto.builder()
                .isInsuranceEnabled(false)
                .isSalaryClient(false)
                .employment(EmploymentDto.builder()
                        .employmentStatus(EmploymentStatus.EMPLOYED)
                        .position(Position.WORKER)
                        .salary(new BigDecimal("100000"))
                        .workExperienceTotal(120)
                        .workExperienceCurrent(24)
                        .employerINN("7707083893")
                        .build())
                .maritalStatus(MaritalStatus.SINGLE)
                .gender(Gender.MALE)
                .birthdate(LocalDate.now().minusYears(29))
                .build();

        BigDecimal rate = calculator.calculateScoringRate(dto);

        assertEquals(new BigDecimal("10.0"), rate);
    }

    @Test
    void shouldNotApplyFemaleAgeDecreaseWhenAgeAboveRange() {
        ScoringProperties props = new ScoringProperties();
        props.setBaseRate(new BigDecimal("10.0"));
        props.setWomanAgeDecrease(new BigDecimal("3.0"));

        RateCalculator calculator = new RateCalculator(props);

        ScoringDataDto dto = ScoringDataDto.builder()
                .isInsuranceEnabled(false)
                .isSalaryClient(false)
                .employment(EmploymentDto.builder()
                        .employmentStatus(EmploymentStatus.EMPLOYED)
                        .position(Position.WORKER)
                        .salary(new BigDecimal("100000"))
                        .workExperienceTotal(120)
                        .workExperienceCurrent(24)
                        .employerINN("7707083893")
                        .build())
                .maritalStatus(MaritalStatus.SINGLE)
                .gender(Gender.FEMALE)
                .birthdate(LocalDate.now().minusYears(61))
                .build();

        BigDecimal rate = calculator.calculateScoringRate(dto);

        assertEquals(new BigDecimal("10.0"), rate);
    }
}