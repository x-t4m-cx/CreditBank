package com.creditbank.calculator.dto.request;

import com.creditbank.calculator.enums.EmploymentStatus;
import com.creditbank.calculator.enums.Position;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
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

    @NotNull(message = "Employment status is required")
    @Schema(example = "EMPLOYED")
    private EmploymentStatus employmentStatus;

    @NotNull(message = "Employer INN is required")
    @Pattern(regexp = "^\\d{10}$|^\\d{12}$",
            message = "Employer INN must be 10 or 12 digits")
    @Schema(description = "Employer INN (10 or 12 digits)", example = "7707083893")
    private String employerINN;

    @NotNull(message = "Salary is required")
    @DecimalMin(value = "0.0", inclusive = false,
            message = "Salary must be greater than 0")
    @DecimalMax(value = "10000000.0",
            message = "Salary must not exceed 10,000,000")
    @Schema(description = "Monthly salary", example = "120000.00")
    private BigDecimal salary;

    @NotNull(message = "Position is required")
    @Schema(example = "MID_MANAGER")
    private Position position;

    @NotNull(message = "Total work experience is required")
    @Min(value = 0, message = "Total work experience cannot be negative")
    @Max(value = 600, message = "Total work experience cannot exceed 600 months (50 years)")
    @Schema(description = "Total work experience in months", example = "120", minimum = "0", maximum = "600")
    private Integer workExperienceTotal;

    @NotNull(message = "Current work experience is required")
    @Min(value = 0, message = "Current work experience cannot be negative")
    @Max(value = 600, message = "Current work experience cannot exceed 600 months (50 years)")
    @Schema(description = "Current work experience in months", example = "24", minimum = "0", maximum = "600")
    private Integer workExperienceCurrent;
}