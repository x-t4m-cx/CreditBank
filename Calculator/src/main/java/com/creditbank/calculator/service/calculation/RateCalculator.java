package com.creditbank.calculator.service.calculation;

import com.creditbank.calculator.config.property.ScoringProperties;
import com.creditbank.calculator.dto.request.ScoringDataDto;
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

        BigDecimal totalRate = scoringProperties.getBaseRate();

        if (isInsuranceEnabled) {
            totalRate = totalRate.subtract(scoringProperties.getInsuranceDecrease());
            log.debug("Insurance enabled: rate decreased to {}", totalRate);
        }
        if (isSalaryClient) {
            totalRate = totalRate.subtract(scoringProperties.getSalaryClientDecrease());
            log.debug("Salary client: rate decreased to {}", totalRate);
        }

        return totalRate;
    }

    public BigDecimal calculateScoringRate(ScoringDataDto scoringDataDto) {

        BigDecimal totalRate = calculatePrescoringRate(
                scoringDataDto.getIsInsuranceEnabled(),
                scoringDataDto.getIsSalaryClient());

        switch (scoringDataDto.getEmployment().getEmploymentStatus()) {
            case SELF_EMPLOYED -> {
                totalRate = totalRate.add(scoringProperties.getSelfEmployedIncrease());
                log.debug("Employment SELF_EMPLOYED: rate increased to {}", totalRate);
            }
            case BUSINESS_OWNER -> {
                totalRate = totalRate.add(scoringProperties.getBusinessOwnerIncrease());
                log.debug("Employment BUSINESS_OWNER: rate increased to {}", totalRate);
            }
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
        }

        int age = AgeUtil.calculateAge(scoringDataDto.getBirthdate());
        log.debug("Calculated client age={}", age);

        switch (scoringDataDto.getGender()) {
            case FEMALE -> {
                if (age >= scoringProperties.getWomanMinAge() && age <= scoringProperties.getWomanMaxAge()) {
                    totalRate = totalRate.subtract(scoringProperties.getWomanAgeDecrease());
                    log.debug("Female age benefit applied: rate decreased to {}", totalRate);
                }
            }
            case MALE -> {
                if (age >= scoringProperties.getManMinAge() && age <= scoringProperties.getManMaxAge()) {
                    totalRate = totalRate.subtract(scoringProperties.getManAgeDecrease());
                    log.debug("Male age benefit applied: rate decreased to {}", totalRate);
                }
            }
            case NON_BINARY -> {
                totalRate = totalRate.add(scoringProperties.getNonBinaryIncrease());
                log.debug("Non-binary increase applied: rate increased to {}", totalRate);
            }
        }

        return totalRate;
    }
}