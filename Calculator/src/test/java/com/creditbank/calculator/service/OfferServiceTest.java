package com.creditbank.calculator.service;
import com.creditbank.calculator.config.property.InsuranceProperties;
import com.creditbank.calculator.dto.request.LoanStatementRequestDto;
import com.creditbank.calculator.dto.response.LoanOfferDto;
import com.creditbank.calculator.service.calculation.PaymentCalculator;
import com.creditbank.calculator.service.calculation.RateCalculator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OfferServiceTest {

    @Mock
    private InsuranceProperties insuranceProperties;

    @Mock
    private RateCalculator rateCalculator;

    @Mock
    private PaymentCalculator paymentCalculator;

    @InjectMocks
    private OfferService offerService;

    @Test
    void shouldGenerateFourOffersAndSortByRateDescending() {
        LoanStatementRequestDto request = LoanStatementRequestDto.builder()
                .amount(new BigDecimal("100000.00"))
                .term(12)
                .firstName("Ivan")
                .lastName("Petrov")
                .middleName("Sergeevich")
                .email("ivan.petrov@example.com")
                .birthdate(java.time.LocalDate.now().minusYears(30))
                .passportSeries("1234")
                .passportNumber("567890")
                .build();

        when(insuranceProperties.getInsuranceCostRate()).thenReturn(new BigDecimal("0.10"));

        when(rateCalculator.calculatePrescoringRate(eq(false), eq(false))).thenReturn(new BigDecimal("15"));
        when(rateCalculator.calculatePrescoringRate(eq(false), eq(true))).thenReturn(new BigDecimal("14"));
        when(rateCalculator.calculatePrescoringRate(eq(true), eq(false))).thenReturn(new BigDecimal("13"));
        when(rateCalculator.calculatePrescoringRate(eq(true), eq(true))).thenReturn(new BigDecimal("12"));

        when(paymentCalculator.calculateMonthlyPayment(any(), any(), any(Integer.class))).thenReturn(new BigDecimal("9000.00"));

        List<LoanOfferDto> offers = offerService.generateOffers(request);

        assertEquals(4, offers.size());
        assertTrue(offers.get(0).getRate().compareTo(offers.get(1).getRate()) >= 0);
        assertTrue(offers.get(1).getRate().compareTo(offers.get(2).getRate()) >= 0);
        assertTrue(offers.get(2).getRate().compareTo(offers.get(3).getRate()) >= 0);

        LoanOfferDto withInsurance = offers.stream()
                .filter(LoanOfferDto::getIsInsuranceEnabled)
                .findFirst()
                .orElseThrow();

        assertEquals(new BigDecimal("110000.00"), withInsurance.getTotalAmount());

        LoanOfferDto withoutInsurance = offers.stream()
                .filter(o -> !o.getIsInsuranceEnabled())
                .findFirst()
                .orElseThrow();

        assertEquals(new BigDecimal("100000.00"), withoutInsurance.getTotalAmount());

        ArgumentCaptor<BigDecimal> amountCaptor = ArgumentCaptor.forClass(BigDecimal.class);
        verify(paymentCalculator, times(4)).calculateMonthlyPayment(amountCaptor.capture(), any(), eq(12));

        assertTrue(amountCaptor.getAllValues().stream().anyMatch(a -> a.compareTo(new BigDecimal("110000.00")) == 0));
    }
}

