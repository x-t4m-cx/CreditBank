package com.creditbank.calculator.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "LoanStatementRequest", description = "Request to generate loan offers")
public class LoanStatementRequestDto {

    @NotNull(message = "Amount is required")
    @Schema(description = "Requested loan amount", example = "250000.00")
    private BigDecimal amount;

    @NotNull(message = "Term is required")
    @Schema(description = "Loan term in months", example = "24")
    private Integer term;

    @NotNull(message = "First name is required")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "First name must contain only latin letters")
    @Schema(example = "Ivan")
    private String firstName;

    @NotNull(message = "Last name is required")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "First name must contain only latin letters")
    @Schema(example = "Petrov")
    private String lastName;

    @Pattern(regexp = "^[a-zA-Z]+$", message = "First name must contain only latin letters")
    @Schema(example = "Sergeevich", nullable = true)
    private String middleName;

    @NotNull(message = "Email is required")
    @Email(message = "Invalid email format")
    @Pattern(regexp = "^[a-z0-9A-Z_!#$%&'*+/=?`{|}~^.-]+@[a-z0-9A-Z.-]+$")
    @Schema(example = "ivan.petrov@example.com")
    private String email;

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
}