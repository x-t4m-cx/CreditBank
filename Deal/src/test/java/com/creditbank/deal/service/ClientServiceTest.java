package com.creditbank.deal.service;

import com.creditbank.deal.dto.EmploymentDto;
import com.creditbank.deal.dto.FinishRegistrationRequestDto;
import com.creditbank.deal.dto.LoanStatementRequestDto;
import com.creditbank.deal.entity.Client;
import com.creditbank.deal.entity.Statement;
import com.creditbank.deal.enums.Gender;
import com.creditbank.deal.enums.MaritalStatus;
import com.creditbank.deal.jsonb.Employment;
import com.creditbank.deal.jsonb.Passport;
import com.creditbank.deal.mapper.EmploymentMapper;
import com.creditbank.deal.repository.ClientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private EmploymentMapper empMapper;
    @Mock
    private ClientRepository repository;

    @InjectMocks
    private ClientService service;
    @Test
    void shouldCreateClientWhenValidRequestProvided() {
        LoanStatementRequestDto request = LoanStatementRequestDto.builder()
                .passportSeries("1234")
                .passportNumber("567891")
                .lastName("Ivanov")
                .firstName("Ivan")
                .middleName("Ivanovich")
                .birthdate(LocalDate.of(2000,5,23))
                .email("ivanov@mail.ru")
                .build();

        Passport passport = Passport.builder()
                .series(request.getPassportSeries())
                .number(request.getPassportNumber())
                .build();

        UUID clientId = UUID.randomUUID();

        Client savedClient = Client.builder()
                .clientId(clientId)
                .lastName(request.getLastName())
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .birthDate(request.getBirthdate())
                .email(request.getEmail())
                .passport(passport)
                .build();

        when(repository.save(any(Client.class))).thenReturn(savedClient);

        Client result = service.createClient(request);

        assertNotNull(result);
        assertEquals(clientId, result.getClientId());
        assertEquals(request.getLastName(), result.getLastName());
        verify(repository).save(any(Client.class));

    }

    @Test
    void shouldUpdateClientWhenValidStatementAndRequestProvided() {
        Statement statement = new Statement();
        Client existClient = new Client();
        existClient.setClientId(UUID.randomUUID());
        existClient.setPassport(Passport.builder().build());
        statement.setClient(existClient);

        FinishRegistrationRequestDto request = FinishRegistrationRequestDto.builder()
                .passportIssueBranch("GY MVD RUSSIA")
                .passportIssueDate(LocalDate.of(2014,5,13))
                .employment(new EmploymentDto())
                .gender(Gender.MALE)
                .maritalStatus(MaritalStatus.SINGLE)
                .dependentAmount(0)
                .accountNumber("40817810099910004312")
                .build();

        Employment employment = new Employment();

        when(empMapper.toModel(any(EmploymentDto.class))).thenReturn(employment);
        when(repository.save(any(Client.class))).thenReturn(existClient);

        service.updateClient(statement, request);

        assertEquals("GY MVD RUSSIA", existClient.getPassport().getIssueBranch());
        assertEquals(LocalDate.of(2014,5,13), existClient.getPassport().getIssueDate());
        assertNotNull(existClient.getEmployment());
        assertEquals("40817810099910004312", existClient.getAccountNumber());
        verify(repository).save(any(Client.class));


    }
}