package com.creditbank.deal.jsonb;

import com.creditbank.deal.enums.EmploymentPosition;
import com.creditbank.deal.enums.EmploymentStatus;

import java.math.BigDecimal;

public class Employment {
    private EmploymentStatus status;
    private String employmentInn;
    private BigDecimal salary;
    private EmploymentPosition position;
    private Integer workExperienceTotal;
    private Integer workExperienceCurrent;
}
