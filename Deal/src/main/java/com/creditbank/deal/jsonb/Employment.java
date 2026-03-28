package com.creditbank.deal.jsonb;

import com.creditbank.deal.enums.EmploymentPosition;
import com.creditbank.deal.enums.EmploymentStatus;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Employment {
    private EmploymentStatus status;
    private String employmentInn;
    private BigDecimal salary;
    private EmploymentPosition position;
    private Integer workExperienceTotal;
    private Integer workExperienceCurrent;
}
