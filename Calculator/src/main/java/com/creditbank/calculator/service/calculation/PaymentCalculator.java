package com.creditbank.calculator.service.calculation;

import com.creditbank.calculator.dto.response.PaymentScheduleElementDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentCalculator {

    private static final int AMOUNT_SCALE = 2;
    private static final int CALCULATION_SCALE = 10;
    private static final BigDecimal MONTHS_IN_YEAR_PERCENT = new BigDecimal("1200");
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    public BigDecimal calculateMonthlyPayment(BigDecimal amount,
                                              BigDecimal rate,
                                              int term) {

        log.info("Calculate monthly payment: amount={}, rate={}, term={}",
                amount, rate, term);

        BigDecimal monthlyRate = calculateMonthlyRate(rate);
        log.debug("Monthly rate={}", monthlyRate);

        BigDecimal rateFactor = BigDecimal.ONE.add(monthlyRate);
        BigDecimal rateFactorPower = rateFactor.pow(term);

        log.debug("Rate factor={}, rate factor power={}",
                rateFactor, rateFactorPower);

        BigDecimal interestPart = amount.multiply(monthlyRate);
        log.debug("Interest part={}", interestPart);

        BigDecimal annuityCoefficient = rateFactorPower
                .divide(rateFactorPower.subtract(BigDecimal.ONE),
                        CALCULATION_SCALE,
                        ROUNDING_MODE);

        log.debug("Annuity coefficient={}", annuityCoefficient);

        BigDecimal totalPayment = interestPart.multiply(annuityCoefficient)
                .setScale(AMOUNT_SCALE, ROUNDING_MODE);

        log.info("Monthly payment calculated={}", totalPayment);

        return totalPayment;
    }

    public List<PaymentScheduleElementDto> getPaymentScheduleList(
            BigDecimal amount,
            BigDecimal rate,
            Integer term,
            BigDecimal totalPayment) {

        log.info("Generate payment schedule: amount={}, rate={}, term={}, monthlyPayment={}",
                amount, rate, term, totalPayment);

        List<PaymentScheduleElementDto> schedule = new ArrayList<>();

        BigDecimal monthlyRate = calculateMonthlyRate(rate);
        BigDecimal remainingDebt = amount;

        log.debug("Monthly rate for schedule={}", monthlyRate);

        for (int number = 1; number <= term; number++) {

            BigDecimal interestPayment = remainingDebt
                    .multiply(monthlyRate)
                    .setScale(AMOUNT_SCALE, ROUNDING_MODE);

            BigDecimal debtPayment = totalPayment
                    .subtract(interestPayment)
                    .setScale(AMOUNT_SCALE, ROUNDING_MODE);

            if (term.equals(number)) {
                log.debug("Last payment adjustment");

                debtPayment = remainingDebt;
                totalPayment = debtPayment
                        .add(interestPayment)
                        .setScale(AMOUNT_SCALE, ROUNDING_MODE);
            }

            remainingDebt = remainingDebt.subtract(debtPayment);

            PaymentScheduleElementDto element = PaymentScheduleElementDto
                    .builder()
                    .number(number)
                    .date(LocalDate.now().plusMonths(number))
                    .totalPayment(totalPayment)
                    .interestPayment(interestPayment)
                    .debtPayment(debtPayment)
                    .remainingDebt(remainingDebt.max(BigDecimal.ZERO))
                    .build();

            log.debug("Schedule element created: {}", element);

            schedule.add(element);
        }

        log.info("Payment schedule generated. Total elements={}", schedule.size());

        return schedule;
    }

    private BigDecimal calculateMonthlyRate(BigDecimal rate) {

        BigDecimal monthlyRate = rate.divide(
                MONTHS_IN_YEAR_PERCENT,
                CALCULATION_SCALE,
                ROUNDING_MODE
        );

        log.debug("Calculated monthly rate from annual rate {} -> {}",
                rate, monthlyRate);

        return monthlyRate;
    }
}