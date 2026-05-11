package com.creditbank.statement.dto.request;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "LoanOffer", description = "Loan offer")
public class LoanOfferDto {

    @Schema(description = "Statement identifier", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID statementId;

    @NotNull(message = "Requested amount is required")
    @DecimalMin(value = "20000.0", message = "Amount must be at least 20000")
    @Schema(description = "Requested amount", example = "250000.00")
    private BigDecimal requestedAmount;

    @DecimalMin(value = "0.00", message = "Total amount must be positive")
    @Schema(description = "Total amount including additional services", example = "275000.00")
    private BigDecimal totalAmount;

    @NotNull(message = "Term is required")
    @Min(value = 6, message = "Term must be at least 6 months")
    @Schema(description = "Loan term in months", example = "24")
    private Integer term;

    @DecimalMin(value = "0.00", message = "Monthly payment must be positive")
    @Schema(description = "Monthly payment", example = "12950.55")
    private BigDecimal monthlyPayment;

    @DecimalMin(value = "0.0", message = "Rate must be positive")
    @Schema(description = "Interest rate", example = "14.5")
    private BigDecimal rate;

    @NotNull(message = "Insurance status is required")
    @Schema(description = "Is insurance enabled", example = "true")
    private Boolean isInsuranceEnabled;

    @NotNull(message = "Salary client status is required")
    @Schema(description = "Is salary client", example = "false")
    private Boolean isSalaryClient;
}