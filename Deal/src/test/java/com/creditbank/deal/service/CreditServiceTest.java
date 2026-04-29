package com.creditbank.deal.service;

import com.creditbank.deal.client.rest.CalculatorClient;
import com.creditbank.deal.dto.calculator.response.CreditDto;
import com.creditbank.deal.dto.response.ErrorResponse;
import com.creditbank.deal.dto.request.FinishRegistrationRequestDto;
import com.creditbank.deal.dto.calculator.request.ScoringDataDto;
import com.creditbank.deal.entity.Client;
import com.creditbank.deal.entity.Credit;
import com.creditbank.deal.entity.Statement;
import com.creditbank.deal.enums.*;
import com.creditbank.deal.exception.DeniedException;
import com.creditbank.deal.entity.jsonb.Employment;
import com.creditbank.deal.entity.jsonb.Passport;
import com.creditbank.deal.mapper.CreditMapper;
import com.creditbank.deal.mapper.ScoringDataMapper;
import com.creditbank.deal.model.LoanOffer;
import com.creditbank.deal.repository.CreditRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreditServiceTest {
    @Mock
    private CalculatorClient calculatorClient;
    @Mock
    private StatementService statementService;
    @Mock
    private ClientService clientService;
    @Mock
    private DocumentService documentService;
    @Mock
    private CreditMapper creditMapper;
    @Mock
    private ScoringDataMapper scoringDataMapper;
    @Mock
    private CreditRepository repository;
    @Mock
    private MeterRegistry meterRegistry;
    @Mock
    private Counter deniedCounter;
    @InjectMocks
    private CreditService service;

    @Test
    void shouldCalculateCreditWhenValidRequestAndStatementProvided() {
        FinishRegistrationRequestDto request = new FinishRegistrationRequestDto();
        String id = UUID.randomUUID().toString();
        Statement statement = new Statement();
        statement.setStatementId(UUID.fromString(id));

        Client client = new Client();

        Passport passport = new Passport();
        passport.setSeries("1234");
        passport.setNumber("567890");
        passport.setIssueDate(LocalDate.of(2000, 5, 11));
        passport.setIssueBranch("MVD RUSSIA");
        client.setPassport(passport);

        LoanOffer offer = new LoanOffer();
        statement.setClient(client);
        statement.setAppliedOffer(offer);

        ScoringDataDto scoringDataDto = new ScoringDataDto();
        CreditDto creditDto = new CreditDto();
        Credit credit = new Credit();
        credit.setCreditStatus(CreditStatus.CALCULATED);

        when(scoringDataMapper.toDto(statement)).thenReturn(scoringDataDto);
        when(statementService.getStatementById(UUID.fromString(id))).thenReturn(statement);
        doNothing().when(clientService).updateClient(any(Statement.class), any(FinishRegistrationRequestDto.class));
        when(calculatorClient.calculateCredit(scoringDataDto)).thenReturn(creditDto);
        when(creditMapper.toEntity(creditDto)).thenReturn(credit);
        when(repository.save(credit)).thenReturn(credit);

        service.calculateCredit(request, id);

        assertNotNull(statement.getCredit());
        assertEquals(CreditStatus.CALCULATED, statement.getCredit().getCreditStatus());
        verify(statementService).setStatus(statement, ApplicationStatus.CC_APPROVED, ChangeType.AUTOMATIC);
        verify(statementService).updateStatement(statement);
        verify(repository).save(credit);
        verify(scoringDataMapper).toDto(statement);
    }

    @Test
    void shouldThrowDeniedExceptionWhenCreditCalculationFails() {
        FinishRegistrationRequestDto request = new FinishRegistrationRequestDto();
        String statementId = UUID.randomUUID().toString();
        Statement statement = new Statement();
        statement.setStatementId(UUID.fromString(statementId));

        Passport passport = Passport.builder()
                .series("1234")
                .number("123456")
                .issueDate(LocalDate.of(2000, 5, 11))
                .issueBranch("MVD RUSSIA")
                .build();

        Client client = Client.builder()
                .firstName("John")
                .lastName("Doe")
                .middleName("Smith")
                .birthDate(LocalDate.of(1990, 1, 1))
                .gender(Gender.MALE)
                .maritalStatus(MaritalStatus.MARRIED)
                .dependentAmount(0)
                .accountNumber("1234567890")
                .passport(passport)
                .build();

        Employment employment = new Employment();
        employment.setEmploymentInn("12334556");
        employment.setPosition(EmploymentPosition.WORKER);
        employment.setSalary(BigDecimal.valueOf(100000));
        client.setEmployment(employment);

        statement.setClient(client);

        LoanOffer offer = new LoanOffer();
        offer.setTerm(12);
        offer.setTotalAmount(BigDecimal.valueOf(100000));
        offer.setInsuranceEnabled(false);
        offer.setSalaryClient(false);
        statement.setAppliedOffer(offer);

        ScoringDataDto scoringDataDto = new ScoringDataDto();
        when(scoringDataMapper.toDto(statement)).thenReturn(scoringDataDto);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("Denied error")
                .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .message("Credit calculation failed")
                .build();

        HttpClientErrorException httpClientErrorException = mock(HttpClientErrorException.class);
        when(httpClientErrorException.getStatusCode()).thenReturn(HttpStatus.UNPROCESSABLE_ENTITY);
        when(httpClientErrorException.getResponseBodyAs(ErrorResponse.class)).thenReturn(errorResponse);

        when(statementService.getStatementById(UUID.fromString(statementId)))
                .thenReturn(statement);
        doNothing().when(clientService).updateClient(any(Statement.class), any(FinishRegistrationRequestDto.class));

        when(calculatorClient.calculateCredit(any(ScoringDataDto.class)))
                .thenThrow(httpClientErrorException);

        DeniedException exception = assertThrows(DeniedException.class,
                () -> service.calculateCredit(request, statementId));

        assertEquals("Credit calculation failed", exception.getMessage());
        verify(statementService).setStatus(statement, ApplicationStatus.CC_DENIED, ChangeType.AUTOMATIC);
        verify(statementService).updateStatement(statement);
        verify(repository, never()).save(any(Credit.class));
    }

    @Test
    void shouldThrowHttpClientErrorExceptionWhenStatusIsNot422() {
        FinishRegistrationRequestDto request = new FinishRegistrationRequestDto();
        String statementId = UUID.randomUUID().toString();
        Statement statement = new Statement();
        statement.setStatementId(UUID.fromString(statementId));

        Passport passport = Passport.builder()
                .series("1234")
                .number("123456")
                .issueDate(LocalDate.of(2000, 5, 11))
                .issueBranch("MVD RUSSIA")
                .build();

        Client client = Client.builder()
                .firstName("John")
                .lastName("Doe")
                .middleName("Smith")
                .birthDate(LocalDate.of(1990, 1, 1))
                .gender(Gender.MALE)
                .maritalStatus(MaritalStatus.MARRIED)
                .dependentAmount(0)
                .accountNumber("1234567890")
                .passport(passport)
                .build();

        statement.setClient(client);

        LoanOffer offer = new LoanOffer();
        offer.setTerm(12);
        offer.setTotalAmount(BigDecimal.valueOf(100000));
        offer.setInsuranceEnabled(false);
        offer.setSalaryClient(false);
        statement.setAppliedOffer(offer);

        ScoringDataDto scoringDataDto = new ScoringDataDto();
        when(scoringDataMapper.toDto(statement)).thenReturn(scoringDataDto);

        HttpClientErrorException httpClientErrorException = mock(HttpClientErrorException.class);
        when(httpClientErrorException.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        when(httpClientErrorException.getResponseBodyAs(String.class)).thenReturn("Validation error");

        when(statementService.getStatementById(UUID.fromString(statementId)))
                .thenReturn(statement);
        doNothing().when(clientService).updateClient(any(Statement.class), any(FinishRegistrationRequestDto.class));

        when(calculatorClient.calculateCredit(any(ScoringDataDto.class)))
                .thenThrow(httpClientErrorException);

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class,
                () -> service.calculateCredit(request, statementId));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Validation error", exception.getResponseBodyAs(String.class));

        verify(statementService, never()).setStatus(any(), any(), any());
        verify(statementService, never()).updateStatement(any());
        verify(repository, never()).save(any(Credit.class));
    }
}