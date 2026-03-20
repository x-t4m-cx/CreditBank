package com.creditbank.calculator.service;

import com.creditbank.calculator.config.property.ScoringProperties;
import com.creditbank.calculator.dto.request.EmploymentDto;
import com.creditbank.calculator.dto.request.ScoringDataDto;
import com.creditbank.calculator.dto.response.CreditDto;
import com.creditbank.calculator.dto.response.PaymentScheduleElementDto;
import com.creditbank.calculator.enums.EmploymentStatus;
import com.creditbank.calculator.enums.Gender;
import com.creditbank.calculator.enums.MaritalStatus;
import com.creditbank.calculator.enums.Position;
import com.creditbank.calculator.exception.ScoringException;
import com.creditbank.calculator.service.calculation.PaymentCalculator;
import com.creditbank.calculator.service.calculation.RateCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CreditServiceTest {

    @Mock
    private PaymentCalculator paymentCalculator;

    @Mock
    private RateCalculator rateCalculator;

    @Mock
    private ScoringProperties scoringProperties;

    @InjectMocks
    private CreditService creditService;

    @BeforeEach
    void setUp() {
        when(scoringProperties.getMinAge()).thenReturn(20);
        when(scoringProperties.getMaxAge()).thenReturn(65);
        when(scoringProperties.getMinTotalExperience()).thenReturn(18);
        when(scoringProperties.getMinCurrentExperience()).thenReturn(3);
        when(scoringProperties.getMaxSalaryMultiplier()).thenReturn(new BigDecimal(24));
    }

    @Test
    void shouldReturnCreditDtoWhenInputIsValid() {
        ScoringDataDto dto = validScoringData()
                .amount(new BigDecimal("100000.00"))
                .term(12)
                .build();

        BigDecimal finalRate = new BigDecimal("12.00");
        BigDecimal monthlyPayment = new BigDecimal("8884.88");

        when(rateCalculator.calculateScoringRate(eq(dto))).thenReturn(finalRate);
        when(paymentCalculator.calculateMonthlyPayment(eq(dto.getAmount()), eq(finalRate), eq(dto.getTerm())))
                .thenReturn(monthlyPayment);
        when(paymentCalculator.getPaymentScheduleList(eq(dto.getAmount()), eq(finalRate), eq(dto.getTerm()), eq(monthlyPayment)))
                .thenReturn(List.of(PaymentScheduleElementDto.builder().number(1).build()));

        CreditDto credit = creditService.calculateCredit(dto);

        assertNotNull(credit);
        assertEquals(dto.getAmount(), credit.getAmount());
        assertEquals(dto.getTerm(), credit.getTerm());
        assertEquals(monthlyPayment, credit.getMonthlyPayment());
        assertEquals(finalRate, credit.getRate());
        assertEquals(monthlyPayment.multiply(new BigDecimal(dto.getTerm())), credit.getPsk());
        assertEquals(dto.getIsInsuranceEnabled(), credit.getIsInsuranceEnabled());
        assertEquals(dto.getIsSalaryClient(), credit.getIsSalaryClient());
        assertEquals(1, credit.getPaymentSchedule().size());
    }

    @Test
    void shouldRejectWhenEmploymentStatusIsUnemployed() {
        ScoringDataDto dto = validScoringData()
                .employment(EmploymentDto.builder()
                        .employmentStatus(EmploymentStatus.UNEMPLOYED)
                        .position(Position.WORKER)
                        .salary(new BigDecimal("100000"))
                        .workExperienceTotal(120)
                        .workExperienceCurrent(24)
                        .employerINN("7707083893")
                        .build())
                .build();

        ScoringException exception = assertThrows(ScoringException.class,
                () -> creditService.calculateCredit(dto));
        assertEquals("Rejection due to: work status - Unemployed", exception.getMessage());
    }

    @Test
    void shouldRejectWhenLoanAmountExceeds24Salaries() {
        ScoringDataDto dto = validScoringData()
                .amount(new BigDecimal("2400001")) // salary=100000 => max=2400000
                .build();

        ScoringException exception = assertThrows(ScoringException.class,
                () -> creditService.calculateCredit(dto));
        assertEquals("Rejection due to: The loan amount is more than 24 salaries Loan amount = 2400001 max allowed = 2400000",
                exception.getMessage());
    }

    @Test
    void shouldRejectWhenAgeIsOutOfRange() {
        ScoringDataDto tooYoung = validScoringData()
                .birthdate(LocalDate.now().minusYears(19))
                .build();

        ScoringException exceptionYoung = assertThrows(ScoringException.class,
                () -> creditService.calculateCredit(tooYoung));
        assertEquals("Rejection due to: age less than 20 or more than 65 years old;the received age = 19",
                exceptionYoung.getMessage());

        ScoringDataDto tooOld = validScoringData()
                .birthdate(LocalDate.now().minusYears(66))
                .build();

        ScoringException exceptionOld = assertThrows(ScoringException.class,
                () -> creditService.calculateCredit(tooOld));
        assertEquals("Rejection due to: age less than 20 or more than 65 years old;the received age = 66",
                exceptionOld.getMessage());
    }

    @Test
    void shouldRejectWhenWorkExperienceIsInsufficient() {
        ScoringDataDto dto = validScoringData()
                .employment(EmploymentDto.builder()
                        .employmentStatus(EmploymentStatus.EMPLOYED)
                        .position(Position.WORKER)
                        .salary(new BigDecimal("100000"))
                        .workExperienceTotal(17)  // Меньше 18
                        .workExperienceCurrent(2)  // Меньше 3
                        .employerINN("7707083893")
                        .build())
                .build();

        ScoringException exception = assertThrows(ScoringException.class,
                () -> creditService.calculateCredit(dto));
        assertEquals("Rejection due to: Total work experience less than 18 months " +
                "or current work experience less than 3 months", exception.getMessage());
    }

    @Test
    void shouldAcceptWhenLoanAmountEquals24Salaries() {
        ScoringDataDto dto = validScoringData()
                .amount(new BigDecimal("2400000"))
                .build();

        BigDecimal finalRate = new BigDecimal("12.00");
        BigDecimal monthlyPayment = new BigDecimal("100000.00");

        when(rateCalculator.calculateScoringRate(eq(dto))).thenReturn(finalRate);
        when(paymentCalculator.calculateMonthlyPayment(eq(dto.getAmount()), eq(finalRate), eq(dto.getTerm())))
                .thenReturn(monthlyPayment);
        when(paymentCalculator.getPaymentScheduleList(eq(dto.getAmount()), eq(finalRate), eq(dto.getTerm()), eq(monthlyPayment)))
                .thenReturn(List.of());

        CreditDto credit = creditService.calculateCredit(dto);

        assertNotNull(credit);
        assertEquals(new BigDecimal("2400000"), credit.getAmount());
    }

    @Test
    void shouldAcceptWhenAgeExactly20() {
        ScoringDataDto dto = validScoringData()
                .birthdate(LocalDate.now().minusYears(20))
                .build();

        when(rateCalculator.calculateScoringRate(eq(dto))).thenReturn(new BigDecimal("12"));
        when(paymentCalculator.calculateMonthlyPayment(any(), any(), anyInt())).thenReturn(new BigDecimal("9000"));
        when(paymentCalculator.getPaymentScheduleList(any(), any(), anyInt(), any())).thenReturn(List.of());

        CreditDto credit = creditService.calculateCredit(dto);

        assertNotNull(credit);
    }

    @Test
    void shouldAcceptWhenAgeExactly65() {
        ScoringDataDto dto = validScoringData()
                .birthdate(LocalDate.now().minusYears(65))
                .build();

        when(rateCalculator.calculateScoringRate(eq(dto))).thenReturn(new BigDecimal("12"));
        when(paymentCalculator.calculateMonthlyPayment(any(), any(), anyInt())).thenReturn(new BigDecimal("9000"));
        when(paymentCalculator.getPaymentScheduleList(any(), any(), anyInt(), any())).thenReturn(List.of());

        CreditDto credit = creditService.calculateCredit(dto);

        assertNotNull(credit);
    }

    @Test
    void shouldRejectWhenOnlyTotalExperienceInsufficient() {
        ScoringDataDto dto = validScoringData()
                .employment(EmploymentDto.builder()
                        .employmentStatus(EmploymentStatus.EMPLOYED)
                        .position(Position.WORKER)
                        .salary(new BigDecimal("100000"))
                        .workExperienceTotal(17)  // Меньше 18
                        .workExperienceCurrent(24)  // Норма
                        .employerINN("7707083893")
                        .build())
                .build();

        ScoringException exception = assertThrows(ScoringException.class,
                () -> creditService.calculateCredit(dto));
        assertEquals("Rejection due to: Total work experience less than 18 months " +
                "or current work experience less than 3 months", exception.getMessage());
    }

    @Test
    void shouldRejectWhenOnlyCurrentExperienceInsufficient() {
        ScoringDataDto dto = validScoringData()
                .employment(EmploymentDto.builder()
                        .employmentStatus(EmploymentStatus.EMPLOYED)
                        .position(Position.WORKER)
                        .salary(new BigDecimal("100000"))
                        .workExperienceTotal(120)  // Норма
                        .workExperienceCurrent(2)  // Меньше 3
                        .employerINN("7707083893")
                        .build())
                .build();

        ScoringException exception = assertThrows(ScoringException.class,
                () -> creditService.calculateCredit(dto));
        assertEquals("Rejection due to: Total work experience less than 18 months " +
                "or current work experience less than 3 months", exception.getMessage());
    }

    private static ScoringDataDto.ScoringDataDtoBuilder validScoringData() {
        return ScoringDataDto.builder()
                .amount(new BigDecimal("100000.00"))
                .term(12)
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
                        .position(Position.WORKER)
                        .salary(new BigDecimal("100000"))
                        .workExperienceTotal(120)
                        .workExperienceCurrent(24)
                        .employerINN("7707083893")
                        .build())
                .accountNumber("40817810099910004312")
                .isInsuranceEnabled(false)
                .isSalaryClient(false);
    }
}