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
public class InsuranceProperties {
    private BigDecimal insuranceCostRate = new BigDecimal("0.10");
}
