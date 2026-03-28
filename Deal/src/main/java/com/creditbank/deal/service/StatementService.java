package com.creditbank.deal.service;

import com.creditbank.deal.dto.LoanStatementRequestDto;
import com.creditbank.deal.entity.Client;
import com.creditbank.deal.entity.Statement;
import com.creditbank.deal.enums.ApplicationStatus;
import com.creditbank.deal.enums.ChangeType;
import com.creditbank.deal.jsonb.StatusHistory;
import com.creditbank.deal.repository.StatementRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatementService {
    private final ClientService clientService;
    private final StatementRepository repository;

    public Statement createStatement(LoanStatementRequestDto request) {
        Client client = clientService.createClient(request);

        Statement statement = Statement.builder()
                .client(client)
                .creationDate(LocalDateTime.now())
                .statusHistory(new ArrayList<>())
                .build();

        setStatus(statement, ApplicationStatus.PREAPPROVAL, ChangeType.AUTOMATIC);
        Statement savedStatement = repository.save(statement);
        log.debug("Statement created - id: {}", savedStatement.getStatementId());
        return savedStatement;
    }

    public Statement getStatementById(UUID statementId) {
        Statement statement = repository.findById(statementId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Statement not found with id: " + statementId));
        log.debug("Statement found - id: {}", statementId);
        return statement;
    }

    public void setStatus(Statement statement, ApplicationStatus status, ChangeType changeType) {
        statement.setStatus(status);
        StatusHistory history = StatusHistory.builder()
                .status(status.name())
                .time(LocalDateTime.now())
                .changeType(changeType)
                .build();
        statement.getStatusHistory().add(history);
    }

    public void updateStatement(Statement statement) {
        Statement updated = repository.save(statement);
        log.debug("Statement updated - id: {}", updated.getStatementId());
    }
}
