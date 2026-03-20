package com.creditbank.calculator.aspect;

import com.creditbank.calculator.dto.request.LoanStatementRequestDto;
import com.creditbank.calculator.dto.response.LoanOfferDto;
import com.creditbank.calculator.service.OfferService;
import com.creditbank.calculator.service.calculation.PaymentCalculator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LoggingAspectTest {

    @Autowired
    private OfferService offerService;

    @Autowired
    private PaymentCalculator paymentCalculator;

    @Test
    void shouldReturnFourOffersWithCorrectUserDataWhenRequestIsValid() {
        LoanStatementRequestDto request = LoanStatementRequestDto.builder()
                .amount(BigDecimal.valueOf(100000))
                .term(12)
                .firstName("Ivanov")
                .lastName("Ivan")
                .build();

        List<LoanOfferDto> offers = offerService.generateOffers(request);

        assertNotNull(offers);
        assertEquals(4, offers.size());
    }

    @Test
    void shouldCalculateCorrectMonthlyPaymentWhenAmountRateAndTermAreValid() {
        BigDecimal amount = BigDecimal.valueOf(100000);
        BigDecimal rate = BigDecimal.valueOf(12);
        int term = 12;

        BigDecimal monthlyPayment = paymentCalculator.calculateMonthlyPayment(amount, rate, term);

        assertNotNull(monthlyPayment);
        assertTrue(monthlyPayment.compareTo(BigDecimal.ZERO) > 0);
        assertEquals(BigDecimal.valueOf(8884.88), monthlyPayment);
    }
}