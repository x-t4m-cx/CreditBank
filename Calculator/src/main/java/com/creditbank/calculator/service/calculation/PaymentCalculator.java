package com.creditbank.calculator.service.calculation;

import com.creditbank.calculator.dto.response.PaymentScheduleElementDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.creditbank.calculator.dictionary.CalculationConstants.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentCalculator {

    public BigDecimal calculateMonthlyPayment(BigDecimal amount,
                                              BigDecimal rate,
                                              int term) {

        BigDecimal monthlyRate = calculateMonthlyRate(rate);
        BigDecimal rateFactor = BigDecimal.ONE.add(monthlyRate);
        BigDecimal rateFactorPower = rateFactor.pow(term);
        BigDecimal interestPart = amount.multiply(monthlyRate);

        BigDecimal annuityCoefficient = rateFactorPower
                .divide(rateFactorPower.subtract(BigDecimal.ONE), CALCULATION_SCALE, ROUNDING_MODE);

        return interestPart
                .multiply(annuityCoefficient)
                .setScale(FINAL_SCALE, ROUNDING_MODE);
    }

    public List<PaymentScheduleElementDto> getPaymentScheduleList(BigDecimal amount, BigDecimal rate, Integer term, BigDecimal totalPayment) {

        List<PaymentScheduleElementDto> schedule = new ArrayList<>();

        BigDecimal monthlyRate = calculateMonthlyRate(rate);

        BigDecimal remainingDebt = amount;
        for (int number = 1; number <= term; number++) {

            //Проценты
            BigDecimal interestPayment = remainingDebt
                    .multiply(monthlyRate)
                    .setScale(FINAL_SCALE, ROUNDING_MODE);
            //Погашено
            BigDecimal debtPayment = totalPayment
                    .subtract(interestPayment)
                    .setScale(FINAL_SCALE, ROUNDING_MODE);

            if (term.equals(number)) {
                debtPayment = remainingDebt;
                totalPayment = debtPayment
                        .add(interestPayment)
                        .setScale(FINAL_SCALE, ROUNDING_MODE);
            }

            //Остаток
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

            schedule.add(element);
        }

        return schedule;
    }

    private BigDecimal calculateMonthlyRate(BigDecimal rate) {
        return rate.divide(MONTHS_IN_YEAR_PERCENT, CALCULATION_SCALE, ROUNDING_MODE);
    }
}