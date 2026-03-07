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
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class OfferService {

    private final InsuranceProperties insuranceProperties;
    private final RateCalculator rateCalculator;
    private final PaymentCalculator paymentCalculator;

    private static final int AMOUNT_SCALE = 2;
    private static final int RATE_SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    public List<LoanOfferDto> generateOffers(LoanStatementRequestDto request) {

        log.info("Start generating loan offers. Input request={}", request);

        List<LoanOfferDto> offers = new ArrayList<>();

        offers.add(createOffer(request, false, false));
        offers.add(createOffer(request, false, true));
        offers.add(createOffer(request, true, false));
        offers.add(createOffer(request, true, true));

        log.debug("Offers before sorting: {}", offers);

        offers.sort(Comparator.comparing(LoanOfferDto::getRate).reversed());

        log.info("Finished generating offers. Result={}", offers);

        return offers;
    }

    private LoanOfferDto createOffer(LoanStatementRequestDto request,
                                     boolean isInsuranceEnabled,
                                     boolean isSalaryClient) {

        log.debug("Creating offer. insuranceEnabled={}, salaryClient={}",
                isInsuranceEnabled, isSalaryClient);

        BigDecimal rate = rateCalculator
                .calculatePrescoringRate(
                isInsuranceEnabled,
                isSalaryClient)
                .setScale(RATE_SCALE, ROUNDING_MODE);

        log.debug("Calculated rate={}", rate);

        BigDecimal totalAmount = request.getAmount();

        if (isInsuranceEnabled) {
            BigDecimal insuranceCost = totalAmount
                    .multiply(insuranceProperties.getInsuranceCostRate())
                    .setScale(AMOUNT_SCALE, ROUNDING_MODE);

            log.debug("Insurance cost calculated={}", insuranceCost);

            totalAmount = totalAmount.add(insuranceCost);

            log.debug("Total amount with insurance={}", totalAmount);
        }

        BigDecimal monthlyPayment = paymentCalculator.calculateMonthlyPayment(
                totalAmount,
                rate,
                request.getTerm()
        );

        log.debug("Monthly payment calculated={}", monthlyPayment);

        LoanOfferDto offer = LoanOfferDto.builder()
                .statementId(UUID.randomUUID())
                .requestedAmount(request.getAmount())
                .totalAmount(totalAmount)
                .term(request.getTerm())
                .monthlyPayment(monthlyPayment)
                .rate(rate)
                .isInsuranceEnabled(isInsuranceEnabled)
                .isSalaryClient(isSalaryClient)
                .build();

        log.debug("Offer created={}", offer);

        return offer;
    }
}