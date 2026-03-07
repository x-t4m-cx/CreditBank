package com.creditbank.calculator.service;

import com.creditbank.calculator.dto.response.CreditDto;
import com.creditbank.calculator.dto.response.PaymentScheduleElementDto;
import com.creditbank.calculator.dto.request.ScoringDataDto;
import com.creditbank.calculator.enums.EmploymentStatus;
import com.creditbank.calculator.exception.ScoringException;
import com.creditbank.calculator.service.calculation.PaymentCalculator;
import com.creditbank.calculator.service.calculation.RateCalculator;
import com.creditbank.calculator.util.AgeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreditService {

    private final PaymentCalculator PaymentCalculator;
    private final RateCalculator rateCalculator;

    private static final int MIN_AGE = 20;
    private static final int MAX_AGE = 65;
    private static final int MIN_TOTAL_EXPERIENCE = 18;
    private static final int MIN_CURRENT_EXPERIENCE = 3;
    private static final BigDecimal MAX_SALARY_MULTIPLIER = BigDecimal.valueOf(24);

    public CreditDto calculateCredit(ScoringDataDto scoringDataDto) {

        log.info("Start calculateCredit. Input data={}", scoringDataDto);

        validateScoringData(scoringDataDto);

        BigDecimal amount = scoringDataDto.getAmount();
        Integer term = scoringDataDto.getTerm();

        log.debug("Scoring parameters: amount={}, term={}", amount, term);

        BigDecimal finalRate = rateCalculator.calculateScoringRate(scoringDataDto);

        log.debug("Calculated final rate={}", finalRate);

        BigDecimal monthlyPayment =
                PaymentCalculator.calculateMonthlyPayment(amount, finalRate, term);

        log.debug("Calculated monthly payment={}", monthlyPayment);

        BigDecimal psk = monthlyPayment.multiply(new BigDecimal(term));

        log.debug("Calculated total payment (PSK)={}", psk);

        List<PaymentScheduleElementDto> schedule =
                PaymentCalculator.getPaymentScheduleList(amount, finalRate, term, monthlyPayment);

        log.debug("Generated payment schedule size={}", schedule.size());

        CreditDto creditDto = CreditDto.builder()
                .amount(amount)
                .term(term)
                .monthlyPayment(monthlyPayment)
                .rate(finalRate)
                .psk(psk)
                .isInsuranceEnabled(scoringDataDto.getIsInsuranceEnabled())
                .isSalaryClient(scoringDataDto.getIsSalaryClient())
                .paymentSchedule(schedule)
                .build();

        log.info("Scoring completed. Result={}", creditDto);

        return creditDto;
    }

    private void validateScoringData(ScoringDataDto scoringDataDto) {

        log.debug("Start calculateCredit validation");

        validateEmploymentStatus(scoringDataDto);
        validateLoanAmount(scoringDataDto);
        validateAge(scoringDataDto);
        validateWorkExperience(scoringDataDto);

        log.debug("Scoring validation completed successfully");
    }

    private void validateEmploymentStatus(ScoringDataDto scoringDataDto) {

        log.debug("Validating employment status");

        if (scoringDataDto.getEmployment().getEmploymentStatus() == EmploymentStatus.UNEMPLOYED) {
            log.debug("Validation failed: employment status is UNEMPLOYED");
            throw new ScoringException("Rejection due to: work status - Unemployed");
        }

        log.debug("Employment status validation passed");
    }

    private void validateLoanAmount(ScoringDataDto scoringDataDto) {

        log.debug("Validating loan amount");

        BigDecimal maxAllowedAmount = scoringDataDto
                .getEmployment()
                .getSalary()
                .multiply(MAX_SALARY_MULTIPLIER);

        boolean isLoanAmountExceeds =
                scoringDataDto.getAmount().compareTo(maxAllowedAmount) > 0;

        log.debug("Loan amount={}, max allowed={}",
                scoringDataDto.getAmount(),
                maxAllowedAmount);

        if (isLoanAmountExceeds) {
            log.debug("Validation failed: loan amount exceeds allowed maximum");
            throw new ScoringException(
                    "Rejection due to: The loan amount is more than 24 salaries"
            );
        }

        log.debug("Loan amount validation passed");
    }

    private void validateAge(ScoringDataDto scoringDataDto) {

        log.debug("Validating client age");

        int age = AgeUtil.calculateAge(scoringDataDto.getBirthdate());

        log.debug("Calculated age={}", age);

        if (age < MIN_AGE || age > MAX_AGE) {
            log.debug("Validation failed: age out of allowed range");
            throw new ScoringException(
                    "Rejection due to: age less than 20 or more than 65 years old"
            );
        }

        log.debug("Age validation passed");
    }

    private void validateWorkExperience(ScoringDataDto scoringDataDto) {

        log.debug("Validating work experience");

        Integer workExperienceTotal =
                scoringDataDto.getEmployment().getWorkExperienceTotal();

        Integer workExperienceCurrent =
                scoringDataDto.getEmployment().getWorkExperienceCurrent();

        log.debug("Work experience total={}, current={}",
                workExperienceTotal, workExperienceCurrent);

        if (workExperienceTotal < MIN_TOTAL_EXPERIENCE ||
                workExperienceCurrent < MIN_CURRENT_EXPERIENCE) {

            log.debug("Validation failed: insufficient work experience");

            throw new ScoringException(
                    "Rejection due to: Total work experience less than 18 months " +
                            "or current work experience less than 3 months"
            );
        }

        log.debug("Work experience validation passed");
    }
}