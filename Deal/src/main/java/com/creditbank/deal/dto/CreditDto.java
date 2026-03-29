package com.creditbank.deal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "Credit", description = "Calculated credit conditions")
public class CreditDto {
    @Schema(description = "Credit amount", example = "250000.00")
    private BigDecimal amount;

    @Schema(description = "Loan term in months", example = "24")
    private Integer term;

    @Schema(description = "Monthly payment", example = "12950.55")
    private BigDecimal monthlyPayment;

    @Schema(description = "Interest rate", example = "14.5")
    private BigDecimal rate;

    @Schema(
            description = "Full cost of credit (PSK = monthlyPayment × term)",
            example = "310813.20"
    )
    private BigDecimal psk;

    @Schema(description = "Is insurance enabled", example = "true")
    private Boolean isInsuranceEnabled;

    @Schema(description = "Is salary client", example = "false")
    private Boolean isSalaryClient;

    @Schema(description = "Payment schedule")
    private List<PaymentScheduleElementDto> paymentSchedule;
}
