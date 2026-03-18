package com.creditbank.calculator.service.calculation;

import com.creditbank.calculator.config.property.ScoringProperties;
import com.creditbank.calculator.dto.request.ScoringDataDto;
import com.creditbank.calculator.exception.ScoringException;
import com.creditbank.calculator.util.AgeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
public class RateCalculator {

    private final ScoringProperties scoringProperties;

    public BigDecimal calculatePrescoringRate(boolean isInsuranceEnabled,
                                              boolean isSalaryClient) {

        log.info("Calculate prescoring rate: insuranceEnabled={}, salaryClient={}",
                isInsuranceEnabled, isSalaryClient);

        BigDecimal totalRate = scoringProperties.getBaseRate();

        log.debug("Base rate={}", totalRate);

        if (isInsuranceEnabled) {
            totalRate = totalRate.subtract(scoringProperties.getInsuranceDecrease());
            log.debug("Insurance enabled: rate decreased to {}", totalRate);
        }

        if (isSalaryClient) {
            totalRate = totalRate.subtract(scoringProperties.getSalaryClientDecrease());
            log.debug("Salary client: rate decreased to {}", totalRate);
        }

        log.info("Prescoring rate calculated={}", totalRate);

        return totalRate;
    }

    public BigDecimal calculateScoringRate(ScoringDataDto scoringDataDto) {

        log.info("Calculate calculateCredit rate for scoringData={}", scoringDataDto);

        BigDecimal totalRate = calculatePrescoringRate(
                scoringDataDto.getIsInsuranceEnabled(),
                scoringDataDto.getIsSalaryClient());

        log.debug("Rate after prescoring={}", totalRate);

        switch (scoringDataDto.getEmployment().getEmploymentStatus()) {

            case SELF_EMPLOYED -> {
                totalRate = totalRate.add(scoringProperties.getSelfEmployedIncrease());
                log.debug("Employment SELF_EMPLOYED: rate increased to {}", totalRate);
            }

            case BUSINESS_OWNER -> {
                totalRate = totalRate.add(scoringProperties.getBusinessOwnerIncrease());
                log.debug("Employment BUSINESS_OWNER: rate increased to {}", totalRate);
            }

            default -> log.debug("Employment status has no rate modification");
        }

        switch (scoringDataDto.getEmployment().getPosition()) {

            case MID_MANAGER -> {
                totalRate = totalRate.subtract(scoringProperties.getMidManagerDecrease());
                log.debug("Position MID_MANAGER: rate decreased to {}", totalRate);
            }

            case TOP_MANAGER -> {
                totalRate = totalRate.subtract(scoringProperties.getTopManagerDecrease());
                log.debug("Position TOP_MANAGER: rate decreased to {}", totalRate);
            }

            default -> log.debug("Position has no rate modification");
        }

        switch (scoringDataDto.getMaritalStatus()) {

            case MARRIED -> {
                totalRate = totalRate.subtract(scoringProperties.getMarriedDecrease());
                log.debug("Marital status MARRIED: rate decreased to {}", totalRate);
            }

            case DIVORCED -> {
                totalRate = totalRate.add(scoringProperties.getDivorcedIncrease());
                log.debug("Marital status DIVORCED: rate increased to {}", totalRate);
            }

            default -> log.debug("Marital status has no rate modification");
        }

        int age = AgeUtil.calculateAge(scoringDataDto.getBirthdate());

        log.debug("Calculated client age={}", age);

        switch (scoringDataDto.getGender()) {

            case FEMALE -> {
                if (age >= 32 && age <= 60) {
                    totalRate = totalRate.subtract(scoringProperties.getWomanAgeDecrease());
                    log.debug("Female age benefit applied: rate decreased to {}", totalRate);
                }
            }

            case MALE -> {
                if (age >= 30 && age <= 55) {
                    totalRate = totalRate.subtract(scoringProperties.getManAgeDecrease());
                    log.debug("Male age benefit applied: rate decreased to {}", totalRate);
                }
            }

            case NON_BINARY -> {
                totalRate = totalRate.add(scoringProperties.getNonBinaryIncrease());
                log.debug("Non-binary increase applied: rate increased to {}", totalRate);
            }
        }

        log.info("Final calculateCredit rate calculated={}", totalRate);

        return totalRate;
    }
}