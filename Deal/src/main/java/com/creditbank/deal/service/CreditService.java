package com.creditbank.deal.service;

import com.creditbank.deal.client.rest.CalculatorClient;
import com.creditbank.deal.dto.calculator.request.ScoringDataDto;
import com.creditbank.deal.dto.calculator.response.CreditDto;
import com.creditbank.deal.dto.request.FinishRegistrationRequestDto;
import com.creditbank.deal.dto.response.ErrorResponse;
import com.creditbank.deal.entity.Credit;
import com.creditbank.deal.entity.Statement;
import com.creditbank.deal.enums.ApplicationStatus;
import com.creditbank.deal.enums.ChangeType;
import com.creditbank.deal.enums.CreditStatus;
import com.creditbank.deal.exception.DeniedException;
import com.creditbank.deal.mapper.CreditMapper;
import com.creditbank.deal.mapper.ScoringDataMapper;
import com.creditbank.deal.repository.CreditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreditService {

    private final CalculatorClient calculatorClient;

    private final StatementService statementService;
    private final ClientService clientService;
    private final DocumentService documentService;
    private final CreditMapper creditMapper;
    private final ScoringDataMapper scoringDataMapper;
    private final CreditRepository repository;

    public void calculateCredit(FinishRegistrationRequestDto request, String statementId) {
        Statement statement = statementService.getStatementById(UUID.fromString(statementId));
        clientService.updateClient(statement, request);

        ScoringDataDto scoringData = scoringDataMapper.toDto(statement);

        try {
            CreditDto creditDto = calculatorClient.calculateCredit(scoringData);
            Credit credit = creditMapper.toEntity(creditDto);

            credit.setCreditStatus(CreditStatus.CALCULATED);
            credit = repository.save(credit);

            log.debug("Credit calculated - id: {}", credit.getCreditId());
            statement.setCredit(credit);
            statementService.setStatus(statement, ApplicationStatus.CC_APPROVED, ChangeType.AUTOMATIC);
            statementService.updateStatement(statement);
            documentService.sendCreateDocumentRequest(UUID.fromString(statementId));

        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {

                String deniedMessage = Objects.requireNonNull(ex.getResponseBodyAs(ErrorResponse.class)).getMessage();
                log.debug("Credit denied - " + deniedMessage);
                statementService.setStatus(statement, ApplicationStatus.CC_DENIED, ChangeType.AUTOMATIC);
                statementService.updateStatement(statement);
                documentService.sendCreditDenied(UUID.fromString(statementId));

                throw new DeniedException(deniedMessage);

            }
            throw ex;
        }
    }

}
