package com.creditbank.calculator.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "PaymentScheduleElement", description = "Single payment schedule item")
public class PaymentScheduleElementDto {
    @Schema(description = "Payment number (1..term)", example = "1")
    private Integer number;

    @Schema(description = "Payment date", example = "2026-04-12")
    private LocalDate date;

    @Schema(description = "Total payment amount", example = "12950.55")
    private BigDecimal totalPayment;

    @Schema(description = "Interest part of payment", example = "3025.12")
    private BigDecimal interestPayment;

    @Schema(description = "Principal part of payment", example = "9925.43")
    private BigDecimal debtPayment;

    @Schema(description = "Remaining principal after payment", example = "240074.57")
    private BigDecimal remainingDebt;
}