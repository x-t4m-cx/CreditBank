package com.creditbank.calculator.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Data
@Component
@ConfigurationProperties(prefix = "calculator.scoring")
public class InsuranceProperties {
    private BigDecimal insuranceCostRate = new BigDecimal("0.10");
}
