package com.creditbank.gateway.dto.request;

import com.creditbank.gateway.enums.Gender;
import com.creditbank.gateway.enums.MaritalStatus;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(example = "MALE")
    private Gender gender;

    @Schema(example = "MARRIED")
    private MaritalStatus maritalStatus;

    @Schema(example = "1", minimum = "0", maximum = "20")
    private Integer dependentAmount;

    @Schema(example = "2015-04-10")
    private LocalDate passportIssueDate;

    @Schema(example = "MVD Russia 770-001")
    private String passportIssueBranch;

    private EmploymentDto employment;

    @Schema(example = "40817810099910004312")
    private String accountNumber;
}