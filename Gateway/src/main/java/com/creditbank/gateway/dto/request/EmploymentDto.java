package com.creditbank.gateway.dto.request;

import com.creditbank.gateway.enums.EmploymentPosition;
import com.creditbank.gateway.enums.EmploymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "Employment", description = "Employment information")
public class EmploymentDto {

    @Schema(example = "EMPLOYED")
    private EmploymentStatus employmentStatus;

    @Schema(description = "Employer INN (10 or 12 digits)", example = "7707083893")
    private String employerINN;

    @Schema(description = "Monthly salary", example = "120000.00")
    private BigDecimal salary;

    @Schema(example = "MID_MANAGER")
    private EmploymentPosition position;


    @Schema(description = "Total work experience in months", example = "120", minimum = "0", maximum = "600")
    private Integer workExperienceTotal;

    @Schema(description = "Current work experience in months", example = "24", minimum = "0", maximum = "600")
    private Integer workExperienceCurrent;
}
