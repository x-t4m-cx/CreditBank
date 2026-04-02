package com.creditbank.deal.dto.request;

import com.creditbank.deal.validation.annotation.Adult;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
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

    @NotNull(message = "Email is required")
    @Email(message = "Invalid email format")
    @Pattern(regexp = "^[a-z0-9A-Z_!#$%&'*+/=?`{|}~^.-]+@[a-z0-9A-Z.-]+$")
    @Schema(example = "ivan.petrov@example.com")
    private String email;

    @NotNull(message = "Birthdate is required")
    @Past(message = "Birthdate must be in the past")
    @Adult
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
