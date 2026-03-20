package com.creditbank.calculator.config.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "calculator.scoring")
public class ScoringProperties {
    private BigDecimal baseRate = new BigDecimal("20.0");

    // Увеличения ставки
    private BigDecimal selfEmployedIncrease = new BigDecimal("2");
    private BigDecimal businessOwnerIncrease = new BigDecimal("1");
    private BigDecimal divorcedIncrease = new BigDecimal("1");
    private BigDecimal nonBinaryIncrease = new BigDecimal("7");

    // Уменьшения ставки
    private BigDecimal insuranceDecrease = new BigDecimal("3.0");
    private BigDecimal salaryClientDecrease  = new BigDecimal("1.0");
    private BigDecimal midManagerDecrease = new BigDecimal("2");
    private BigDecimal topManagerDecrease = new BigDecimal("3");
    private BigDecimal marriedDecrease = new BigDecimal("3");
    private BigDecimal womanAgeDecrease = new BigDecimal("3");
    private BigDecimal manAgeDecrease = new BigDecimal("3");

    // Возрастные границы
    private Integer womanMinAge = 32;
    private Integer womanMaxAge = 60;
    private Integer manMinAge = 30;
    private Integer manMaxAge = 55;
    private Integer minAge = 20;
    private Integer maxAge = 65;

    // Ограничения
    private Integer minTotalExperience = 18;
    private Integer minCurrentExperience = 3;
    private BigDecimal maxSalaryMultiplier = new BigDecimal("24");
}
