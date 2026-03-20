package com.creditbank.calculator.service.calculation;

import com.creditbank.calculator.dto.response.PaymentScheduleElementDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class PaymentCalculatorTest {

    @InjectMocks
    private PaymentCalculator paymentCalculator;

    @Test
    void shouldCalculateMonthlyPaymentWhenValidInputProvided() {

        BigDecimal amount = new BigDecimal("100000");
        BigDecimal rate = new BigDecimal("12");
        int term = 12;

        BigDecimal result = paymentCalculator.calculateMonthlyPayment(amount, rate, term);

        assertEquals(new BigDecimal("8884.88"), result);
    }

    @Test
    void shouldGenerateScheduleWithCorrectSizeWhenValidInputProvided() {

        BigDecimal amount = new BigDecimal("100000");
        BigDecimal rate = new BigDecimal("12");
        int term = 12;

        BigDecimal monthlyPayment =
                paymentCalculator.calculateMonthlyPayment(amount, rate, term);

        List<PaymentScheduleElementDto> schedule =
                paymentCalculator.getPaymentScheduleList(amount, rate, term, monthlyPayment);

        assertEquals(12, schedule.size());
    }

    @Test
    void shouldCalculateCorrectFirstPaymentWhenScheduleGenerated() {

        BigDecimal amount = new BigDecimal("100000");
        BigDecimal rate = new BigDecimal("12");
        int term = 12;

        BigDecimal monthlyPayment =
                paymentCalculator.calculateMonthlyPayment(amount, rate, term);

        List<PaymentScheduleElementDto> schedule =
                paymentCalculator.getPaymentScheduleList(amount, rate, term, monthlyPayment);

        PaymentScheduleElementDto first = schedule.getFirst();

        assertEquals(1, first.getNumber());
        assertEquals(new BigDecimal("1000.00"), first.getInterestPayment());
        assertEquals(new BigDecimal("7884.88"), first.getDebtPayment());
    }

    @Test
    void shouldCloseRemainingDebtAfterLastPayment() {

        BigDecimal amount = new BigDecimal("100000");
        BigDecimal rate = new BigDecimal("12");
        int term = 12;

        BigDecimal monthlyPayment =
                paymentCalculator.calculateMonthlyPayment(amount, rate, term);

        List<PaymentScheduleElementDto> schedule =
                paymentCalculator.getPaymentScheduleList(amount, rate, term, monthlyPayment);

        PaymentScheduleElementDto last = schedule.get(term - 1);

        assertEquals(term, last.getNumber());
        assertEquals(0, last.getRemainingDebt().compareTo(BigDecimal.ZERO));
    }
}