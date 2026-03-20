package com.creditbank.calculator.service;


import com.creditbank.calculator.config.property.InsuranceProperties;
import com.creditbank.calculator.dto.response.LoanOfferDto;
import com.creditbank.calculator.dto.request.LoanStatementRequestDto;
import com.creditbank.calculator.service.calculation.PaymentCalculator;
import com.creditbank.calculator.service.calculation.RateCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static com.creditbank.calculator.dictionary.CalculationConstants.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class OfferService {

    private final InsuranceProperties insuranceProperties;
    private final RateCalculator rateCalculator;
    private final PaymentCalculator paymentCalculator;

    public List<LoanOfferDto> generateOffers(LoanStatementRequestDto request) {

        List<LoanOfferDto> offers = new ArrayList<>();

        offers.add(createOffer(request, false, false));
        offers.add(createOffer(request, false, true));
        offers.add(createOffer(request, true, false));
        offers.add(createOffer(request, true, true));

        offers.sort(Comparator.comparing(LoanOfferDto::getRate).reversed());

        return offers;
    }

    private LoanOfferDto createOffer(LoanStatementRequestDto request,
                                     boolean isInsuranceEnabled,
                                     boolean isSalaryClient) {
        BigDecimal rate = rateCalculator
                .calculatePrescoringRate(isInsuranceEnabled, isSalaryClient)
                .setScale(FINAL_SCALE, ROUNDING_MODE);

        BigDecimal totalAmount = request.getAmount();

        if (isInsuranceEnabled) {
            BigDecimal insuranceCost = totalAmount
                    .multiply(insuranceProperties.getInsuranceCostRate())
                    .setScale(FINAL_SCALE, ROUNDING_MODE);

            log.debug("Insurance cost calculated={}", insuranceCost);

            totalAmount = totalAmount.add(insuranceCost);

            log.debug("Total amount with insurance={}", totalAmount);
        }

        BigDecimal monthlyPayment = paymentCalculator.calculateMonthlyPayment(
                totalAmount,
                rate,
                request.getTerm()
        );

        return LoanOfferDto.builder()
                .statementId(UUID.randomUUID())
                .requestedAmount(request.getAmount())
                .totalAmount(totalAmount)
                .term(request.getTerm())
                .monthlyPayment(monthlyPayment)
                .rate(rate)
                .isInsuranceEnabled(isInsuranceEnabled)
                .isSalaryClient(isSalaryClient)
                .build();
    }
}