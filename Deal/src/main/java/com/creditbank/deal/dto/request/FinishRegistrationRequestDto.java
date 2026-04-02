package com.creditbank.deal.dto.request;

import com.creditbank.deal.enums.Gender;
import com.creditbank.deal.enums.MaritalStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString(exclude = {"passportIssueDate", "passportIssueBranch"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "FinishRegistrationRequest", description = "finished registration information")
public class FinishRegistrationRequestDto {

    @NotNull(message = "Gender is required")
    @Schema(example = "MALE")
    private Gender gender;

    @NotNull(message = "Marital status is required")
    @Schema(example = "MARRIED")
    private MaritalStatus maritalStatus;

    @Min(value = 0, message = "Dependent amount cannot be negative")
    @Max(value = 20, message = "Dependent amount cannot exceed 20")
    @Schema(example = "1", minimum = "0", maximum = "20")
    private Integer dependentAmount;

    @NotNull(message = "Passport issue date is required")
    @Past(message = "Passport issue date must be in the past")
    @Schema(example = "2015-04-10")
    private LocalDate passportIssueDate;

    @NotNull(message = "Passport issue branch is required")
    @NotBlank(message = "Passport issue branch cannot be blank")
    @Size(min = 3, max = 50, message = "Passport issue branch must be between 3 and 50 characters")
    @Schema(example = "MVD Russia 770-001")
    private String passportIssueBranch;

    @NotNull(message = "Employment information is required")
    @Valid
    private EmploymentDto employment;

    @NotNull(message = "Account number is required")
    @Pattern(regexp = "^\\d{20}$", message = "Account number must be 20 digits")
    @Schema(example = "40817810099910004312")
    private String accountNumber;
}
