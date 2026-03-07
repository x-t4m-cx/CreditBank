package com.creditbank.calculator.dto.request;

import com.creditbank.calculator.enums.Gender;
import com.creditbank.calculator.enums.MaritalStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ScoringData", description = "Request for credit calculateCredit and calculation")
public class ScoringDataDto {
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "20000.0", message = "Amount must be at least 20000")
    @Schema(description = "Requested loan amount", example = "250000.00", minimum = "20000")
    private BigDecimal amount;

    @NotNull(message = "Term is required")
    @Min(value = 6, message = "Term must be at least 6 months")
    @Schema(description = "Loan term in months", example = "24", minimum = "6")
    private Integer term;

    @NotNull(message = "First name is required")
    @Pattern(regexp = "^[a-zA-Z]{2,30}$", message = "First name must be 2-30 latin letters")
    @Schema(example = "Ivan")
    private String firstName;

    @NotNull(message = "Last name is required")
    @Pattern(regexp = "^[a-zA-Z]{2,30}$", message = "Last name must be 2-30 latin letters")
    @Schema(example = "Petrov")
    private String lastName;

    @Pattern(regexp = "^[a-zA-Z]{2,30}$", message = "Middle name must be 2-30 latin letters")
    @Schema(example = "Sergeevich", nullable = true)
    private String middleName;

    @NotNull(message = "Gender is required")
    @Schema(example = "MALE")
    private Gender gender;

    @NotNull(message = "Birthdate is required")
    @Past(message = "Birthdate must be in the past")
    @Schema(description = "Date of birth", example = "1995-03-12")
    private LocalDate birthdate;

    @NotNull(message = "Passport series is required")
    @Pattern(regexp = "^\\d{4}$", message = "Passport series must be 4 digits")
    @Schema(example = "1234")
    private String passportSeries;

    @NotNull(message = "Passport number is required")
    @Pattern(regexp = "^\\d{6}$", message = "Passport number must be 6 digits")
    @Schema(example = "567890")
    private String passportNumber;

    @NotNull(message = "Passport issue date is required")
    @Past(message = "Passport issue date must be in the past")
    @Schema(example = "2015-04-10")
    private LocalDate passportIssueDate;

    @NotNull(message = "Passport issue branch is required")
    @NotBlank(message = "Passport issue branch cannot be blank")
    @Size(min = 3, max = 50, message = "Passport issue branch must be between 3 and 50 characters")
    @Schema(example = "MVD Russia 770-001")
    private String passportIssueBranch;

    @NotNull(message = "Marital status is required")
    @Schema(example = "MARRIED")
    private MaritalStatus maritalStatus;

    @Min(value = 0, message = "Dependent amount cannot be negative")
    @Max(value = 20, message = "Dependent amount cannot exceed 20")
    @Schema(example = "1", minimum = "0", maximum = "20")
    private Integer dependentAmount;

    @NotNull(message = "Employment information is required")
    @Valid
    private EmploymentDto employment;

    @NotNull(message = "Account number is required")
    @Pattern(regexp = "^\\d{20}$", message = "Account number must be 20 digits")
    @Schema(example = "40817810099910004312")
    private String accountNumber;

    @NotNull(message = "Insurance flag is required")
    @Schema(example = "true")
    private Boolean isInsuranceEnabled;

    @NotNull(message = "Salary client flag is required")
    @Schema(example = "false")
    private Boolean isSalaryClient;

}