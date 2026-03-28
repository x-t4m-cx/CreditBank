package com.creditbank.deal.service;

import com.creditbank.deal.client.CalculatorClient;
import com.creditbank.deal.dto.*;
import com.creditbank.deal.entity.Client;
import com.creditbank.deal.entity.Credit;
import com.creditbank.deal.entity.Statement;
import com.creditbank.deal.enums.ApplicationStatus;
import com.creditbank.deal.enums.ChangeType;
import com.creditbank.deal.enums.CreditStatus;
import com.creditbank.deal.exception.DeniedException;
import com.creditbank.deal.jsonb.Passport;
import com.creditbank.deal.mapper.CreditMapper;
import com.creditbank.deal.mapper.EmploymentMapper;
import com.creditbank.deal.model.LoanOffer;
import com.creditbank.deal.repository.CreditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final EmploymentMapper empMapper;
    private final CreditMapper creditMapper;
    private final CreditRepository repository;

    public void calculateCredit(FinishRegistrationRequestDto request, String statementId) {
        Statement statement = statementService.getStatementById(UUID.fromString(statementId));
        clientService.updateClient(statement, request);
        statement = statementService.getStatementById(UUID.fromString(statementId));

        LoanOffer offer = statement.getAppliedOffer();
        Client client = statement.getClient();

        ScoringDataDto scoringData = saturateWithInfo(client, offer);

        try {
            CreditDto creditDto = calculatorClient.calculateCredit(scoringData);
            Credit credit = creditMapper.toEntity(creditDto);

            credit.setCreditStatus(CreditStatus.CALCULATED);
            credit = repository.save(credit);

            log.debug("Credit calculated - credit: {}", credit);
            statement.setCredit(credit);
            statementService.setStatus(statement, ApplicationStatus.CC_APPROVED, ChangeType.AUTOMATIC);
            statementService.updateStatement(statement);

        } catch (HttpClientErrorException.UnprocessableEntity ex) {
            String deniedMessage = Objects.requireNonNull(ex.getResponseBodyAs(ErrorResponse.class)).getMessage();
            log.debug("Credit denied - " + deniedMessage);
            statementService.setStatus(statement, ApplicationStatus.CC_DENIED, ChangeType.AUTOMATIC);
            statementService.updateStatement(statement);
            throw new DeniedException(deniedMessage);
        }

    }


    private ScoringDataDto saturateWithInfo(Client client, LoanOffer offer) {
        Passport passport = client.getPassport();
        EmploymentDto empDto = empMapper.toDto(client.getEmployment());
        return ScoringDataDto.builder()
                .amount(offer.getTotalAmount())
                .term(offer.getTerm())
                .firstName(client.getFirstName())
                .lastName(client.getLastName())
                .middleName(client.getMiddleName())
                .gender(client.getGender())
                .birthdate(client.getBirthDate())
                .passportSeries(passport.getSeries())
                .passportNumber(passport.getNumber())
                .passportIssueDate(passport.getIssueDate())
                .passportIssueBranch(passport.getIssueBranch())
                .maritalStatus(client.getMaritalStatus())
                .dependentAmount(client.getDependentAmount())
                .employment(empDto)
                .accountNumber(client.getAccountNumber())
                .isInsuranceEnabled(offer.getInsuranceEnabled())
                .isSalaryClient(offer.getSalaryClient())
                .build();
    }
}
