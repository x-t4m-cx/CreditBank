package com.creditbank.deal.service;

import com.creditbank.deal.dto.request.LoanStatementRequestDto;
import com.creditbank.deal.dto.response.StatementDto;
import com.creditbank.deal.entity.Client;
import com.creditbank.deal.entity.Statement;
import com.creditbank.deal.enums.ApplicationStatus;
import com.creditbank.deal.enums.ChangeType;
import com.creditbank.deal.exception.VerifyException;
import com.creditbank.deal.mapper.StatementMapper;
import com.creditbank.deal.mapper.StatusHistoryMapper;
import com.creditbank.deal.repository.StatementRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatementService {
    private final ClientService clientService;
    private final StatementRepository repository;
    private final StatementMapper statementMapper;
    private final StatusHistoryMapper statusHistoryMapper;

    public Statement createStatement(LoanStatementRequestDto request) {
        Client client = clientService.createClient(request);

        Statement statement = statementMapper.toEntity(client);

        setStatus(statement, ApplicationStatus.PREAPPROVAL, ChangeType.AUTOMATIC);
        Statement savedStatement = repository.save(statement);
        log.debug("Statement created - id: {}", savedStatement.getStatementId());
        return savedStatement;
    }
    // Пагинация; findAll() ресурсоемкий запрос - подумать
    public List<StatementDto> getAllStatements(Pageable pageable) {
        return repository.findAll(pageable).stream()
                .map(statementMapper::toDto)
                .toList();
    }
    public Statement getStatementById(UUID statementId) {
        Statement statement = repository.findById(statementId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Statement not found with id: " + statementId));
        log.debug("Statement found - id: {}", statementId);
        return statement;
    }
    public StatementDto getStatementById(String statementId){
        Statement statement = getStatementById(UUID.fromString(statementId));
        return statementMapper.toDto(statement);
    }
    public Statement getStatementByIdWithLock(UUID statementId) {
        Statement statement = repository.findByIdWithLock(statementId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Statement not found with id: " + statementId));
        log.debug("Statement locked and found - id: {}", statementId);
        return statement;
    }

    public void setStatus(Statement statement, ApplicationStatus status, ChangeType changeType) {
        statement.setStatus(status);
        var history = statusHistoryMapper.toEntity(status, changeType);
        statement.getStatusHistory().add(history);
    }
    public void updateStatementWithSesCode(Statement statement) {
        String sesCode = String.format("%04d",
                ThreadLocalRandom.current().nextInt(1000, 10000));
        statement.setSesCode(sesCode);

        updateStatement(statement);
    }
    public void updateStatement(Statement statement) {
        Statement updated = repository.save(statement);
        log.debug("Statement updated - id: {}", updated.getStatementId());
    }

    public void verifyCode(UUID statementId, String code) {
        Statement statement = getStatementById(statementId);
        String trueCode = statement.getSesCode();
        if (!code.equals(trueCode)) {
            throw new VerifyException("The codes do not match");
        }
    }


}
