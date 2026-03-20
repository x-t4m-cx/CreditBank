package com.creditbank.calculator.service;

import com.creditbank.calculator.config.property.ScoringProperties;
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
    private final ScoringProperties scoringProperties;

    public CreditDto calculateCredit(ScoringDataDto scoringDataDto) {

        validateScoringData(scoringDataDto);

        BigDecimal amount = scoringDataDto.getAmount();
        Integer term = scoringDataDto.getTerm();
        BigDecimal finalRate = rateCalculator
                .calculateScoringRate(scoringDataDto);
        BigDecimal monthlyPayment = PaymentCalculator
                .calculateMonthlyPayment(amount, finalRate, term);
        BigDecimal psk = monthlyPayment.multiply(new BigDecimal(term));
        log.debug("Calculated total payment (PSK)={}", psk);

        List<PaymentScheduleElementDto> schedule =
                PaymentCalculator.getPaymentScheduleList(amount, finalRate, term, monthlyPayment);

        return CreditDto.builder()
                .amount(amount)
                .term(term)
                .monthlyPayment(monthlyPayment)
                .rate(finalRate)
                .psk(psk)
                .isInsuranceEnabled(scoringDataDto.getIsInsuranceEnabled())
                .isSalaryClient(scoringDataDto.getIsSalaryClient())
                .paymentSchedule(schedule)
                .build();
    }

    private void validateScoringData(ScoringDataDto scoringDataDto) {
        validateEmploymentStatus(scoringDataDto);
        validateLoanAmount(scoringDataDto);
        validateAge(scoringDataDto);
        validateWorkExperience(scoringDataDto);
    }

    private void validateEmploymentStatus(ScoringDataDto scoringDataDto) {
        if (scoringDataDto.getEmployment().getEmploymentStatus() == EmploymentStatus.UNEMPLOYED) {
            throw new ScoringException("Rejection due to: work status - Unemployed");
        }
    }

    private void validateLoanAmount(ScoringDataDto scoringDataDto) {
        BigDecimal maxAllowedAmount = scoringDataDto
                .getEmployment()
                .getSalary()
                .multiply(scoringProperties.getMaxSalaryMultiplier());

        boolean isLoanAmountExceeds =
                scoringDataDto.getAmount().compareTo(maxAllowedAmount) > 0;

        if (isLoanAmountExceeds) {
            throw new ScoringException(
                    "Rejection due to: The loan amount is more than 24 salaries " +
                    "Loan amount = " + scoringDataDto.getAmount() +
                    " max allowed = " + maxAllowedAmount
            );
        }
    }

    private void validateAge(ScoringDataDto scoringDataDto) {

        int age = AgeUtil.calculateAge(scoringDataDto.getBirthdate());

        if (age < scoringProperties.getMinAge() || age > scoringProperties.getMaxAge()) {
            log.debug("Validation failed: age out of allowed range");
            throw new ScoringException(
                    "Rejection due to: age less than 20 or more than 65 years old;" +
                            "the received age = " + age
            );
        }
    }

    private void validateWorkExperience(ScoringDataDto scoringDataDto) {

        Integer workExperienceTotal =
                scoringDataDto.getEmployment().getWorkExperienceTotal();
        Integer workExperienceCurrent =
                scoringDataDto.getEmployment().getWorkExperienceCurrent();

        if (workExperienceTotal < scoringProperties.getMinTotalExperience() ||
                workExperienceCurrent < scoringProperties.getMinCurrentExperience()) {
            throw new ScoringException(
                    "Rejection due to: Total work experience less than 18 months " +
                            "or current work experience less than 3 months"
            );
        }
    }
}