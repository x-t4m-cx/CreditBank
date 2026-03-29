package com.creditbank.deal.service;

import com.creditbank.deal.dto.LoanStatementRequestDto;
import com.creditbank.deal.entity.Client;
import com.creditbank.deal.entity.Statement;
import com.creditbank.deal.enums.ApplicationStatus;
import com.creditbank.deal.enums.ChangeType;
import com.creditbank.deal.repository.StatementRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatementServiceTest {

    @Mock
    private ClientService clientService;
    @Mock
    private StatementRepository repository;
    @InjectMocks
    private StatementService service;

    @Test
    void shouldCreateStatementWhenValidRequestProvided() {
        LoanStatementRequestDto request = LoanStatementRequestDto.builder()
                .passportSeries("1234")
                .passportNumber("123456")
                .lastName("Ivanov")
                .firstName("Ivan")
                .build();
        Client client = new Client();
        client.setClientId(UUID.randomUUID());
        when(clientService.createClient(any(LoanStatementRequestDto.class))).thenReturn(client);

        Statement savedStatement = Statement.builder()
                .statementId(UUID.randomUUID())
                .client(client)
                .status(ApplicationStatus.PREAPPROVAL)
                .creationDate(LocalDateTime.now())
                .statusHistory(new ArrayList<>())
                .build();

        when(repository.save(any(Statement.class))).thenReturn(savedStatement);

        Statement result = service.createStatement(request);
        assertNotNull(result);
        assertNotNull(result.getStatementId());
        assertEquals(client, result.getClient());
        assertEquals(ApplicationStatus.PREAPPROVAL, result.getStatus());
        verify(repository).save(any(Statement.class));
    }

    @Test
    void shouldReturnStatementWhenValidIdProvided() {
        UUID id = UUID.randomUUID();
        Statement statement = new Statement();
        statement.setStatementId(id);

        when(repository.findById(id)).thenReturn(Optional.of(statement));
        Statement result = service.getStatementById(id);

        assertNotNull(result);
        assertEquals(id, result.getStatementId());
        verify(repository).findById(id);
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenStatementNotFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getStatementById(id));
    }

    @Test
    void shouldSetStatusWhenValidStatusProvided() {
        Statement statement = new Statement();
        statement.setStatusHistory(new ArrayList<>());
        ApplicationStatus status = ApplicationStatus.APPROVED;
        ChangeType changeType = ChangeType.MANUAL;

        service.setStatus(statement, status, changeType);
        assertEquals(status, statement.getStatus());
        assertEquals(1, statement.getStatusHistory().size());
        assertEquals(status.name(), statement.getStatusHistory().getFirst().getStatus());
        assertEquals(changeType, statement.getStatusHistory().getFirst().getChangeType());

    }

    @Test
    void shouldUpdateStatementWhenValidStatementProvided() {
        Statement statement = new Statement();
        statement.setStatementId(UUID.randomUUID());

        when(repository.save(any(Statement.class))).thenReturn(statement);

        service.updateStatement(statement);

        verify(repository).save(statement);
    }
}