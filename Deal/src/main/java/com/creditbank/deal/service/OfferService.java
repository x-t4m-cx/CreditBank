package com.creditbank.deal.service;

import com.creditbank.deal.client.CalculatorClient;
import com.creditbank.deal.dto.response.LoanOfferDto;
import com.creditbank.deal.dto.request.LoanStatementRequestDto;
import com.creditbank.deal.entity.Statement;
import com.creditbank.deal.enums.ApplicationStatus;
import com.creditbank.deal.enums.ChangeType;
import com.creditbank.deal.mapper.LoanOfferMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OfferService {
    private final CalculatorClient calculatorClient;
    private final StatementService statementService;
    private final LoanOfferMapper mapper;

    public List<LoanOfferDto> generateOffers(LoanStatementRequestDto request, Statement statement) {
        List<LoanOfferDto> offers = calculatorClient.generateOffers(request);
        log.debug("Offers generated - size: {}", offers.size());
        offers.forEach(offer -> offer.setStatementId(statement.getStatementId()));

        return offers;
    }

    public void applyOffer(LoanOfferDto offer) {
        Statement statement = statementService.getStatementById(offer.getStatementId());

        statement.setAppliedOffer(mapper.toModel(offer));
        statementService.setStatus(statement, ApplicationStatus.APPROVED, ChangeType.MANUAL);

        statementService.updateStatement(statement);
    }
}
