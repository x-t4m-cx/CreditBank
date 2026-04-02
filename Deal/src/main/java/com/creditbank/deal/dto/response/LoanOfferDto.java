package com.creditbank.deal.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "Requested amount", example = "250000.00")
    private BigDecimal requestedAmount;

    @Schema(description = "Total amount including additional services", example = "275000.00")
    private BigDecimal totalAmount;

    @Schema(description = "Loan term in months", example = "24")
    private Integer term;

    @Schema(description = "Monthly payment", example = "12950.55")
    private BigDecimal monthlyPayment;

    @Schema(description = "Interest rate", example = "14.5")
    private BigDecimal rate;

    @Schema(description = "Is insurance enabled", example = "true")
    private Boolean isInsuranceEnabled;

    @Schema(description = "Is salary client", example = "false")
    private Boolean isSalaryClient;
}