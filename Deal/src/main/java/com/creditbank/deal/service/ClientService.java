package com.creditbank.deal.service;

import com.creditbank.deal.dto.FinishRegistrationRequestDto;
import com.creditbank.deal.dto.LoanStatementRequestDto;
import com.creditbank.deal.entity.Client;
import com.creditbank.deal.entity.Statement;
import com.creditbank.deal.jsonb.Employment;
import com.creditbank.deal.jsonb.Passport;
import com.creditbank.deal.mapper.EmploymentMapper;
import com.creditbank.deal.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientService {
    private final EmploymentMapper empMapper;
    private final ClientRepository repository;

    public Client createClient(LoanStatementRequestDto request) {
        Passport passport = Passport.builder()
                .series(request.getPassportSeries())
                .number(request.getPassportNumber())
                .build();

        Client client = Client.builder()
                .lastName(request.getLastName())
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .birthDate(request.getBirthdate())
                .email(request.getEmail())
                .passport(passport)
                .build();

        Client savedClient = repository.save(client);
        log.debug("Client created - id: {}", savedClient.getClientId());
        return savedClient;
    }

    public void updateClient(Statement statement, FinishRegistrationRequestDto request) {
        Client client = statement.getClient();

        Passport passport = client.getPassport();
        passport.setIssueBranch(request.getPassportIssueBranch());
        passport.setIssueDate(request.getPassportIssueDate());

        Employment employment = empMapper.toModel(request.getEmployment());

        client.setGender(request.getGender());
        client.setMaritalStatus(request.getMaritalStatus());
        client.setDependentAmount(request.getDependentAmount());
        client.setPassport(passport);
        client.setEmployment(employment);
        client.setAccountNumber(request.getAccountNumber());


        Client updatedClient = repository.save(client);
        statement.setClient(updatedClient);
        log.debug("Client updated - id: {}", updatedClient.getClientId());
    }
}
