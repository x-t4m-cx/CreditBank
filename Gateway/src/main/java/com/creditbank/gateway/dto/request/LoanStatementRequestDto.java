package com.creditbank.gateway.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@ToString(exclude = {"passportSeries", "passportNumber"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "LoanStatementRequest", description = "Request to generate loan offers")
public class LoanStatementRequestDto {

    @Schema(description = "Requested loan amount", example = "250000.00", minimum = "20000")
    private BigDecimal amount;

    @Schema(description = "Loan term in months", example = "24", minimum = "6")
    private Integer term;

    @Schema(example = "Ivan")
    private String firstName;

    @Schema(example = "Petrov")
    private String lastName;

    @Schema(example = "Sergeevich", nullable = true)
    private String middleName;

    @Schema(example = "ivan.petrov@example.com")
    private String email;

    @Schema(description = "Date of birth", example = "1995-03-12")
    private LocalDate birthdate;

    @Schema(example = "1234")
    private String passportSeries;

    @Schema(example = "567890")
    private String passportNumber;
}