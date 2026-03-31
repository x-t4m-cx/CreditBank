package com.creditbank.deal.service;

import com.creditbank.deal.dto.FinishRegistrationRequestDto;
import com.creditbank.deal.dto.LoanStatementRequestDto;
import com.creditbank.deal.entity.Client;
import com.creditbank.deal.entity.Statement;
import com.creditbank.deal.jsonb.Passport;
import com.creditbank.deal.mapper.ClientMapper;
import com.creditbank.deal.mapper.PassportMapper;
import com.creditbank.deal.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientService {
    private final ClientMapper clientMapper;
    private final PassportMapper passportMapper;
    private final ClientRepository repository;

    public Client createClient(LoanStatementRequestDto request) {
        Passport passport = passportMapper.toModel(request);
        Client client = clientMapper.toEntity(request);
        client.setPassport(passport);

        Client savedClient = repository.save(client);
        log.debug("Client created - id: {}", savedClient.getClientId());
        return savedClient;
    }

    public void updateClient(Statement statement, FinishRegistrationRequestDto request) {
        Client client = statement.getClient();

        passportMapper.updateModel(client.getPassport(), request);
        clientMapper.updateEntity(client, request);

        Client updatedClient = repository.save(client);
        statement.setClient(updatedClient);
        log.debug("Client updated - id: {}", updatedClient.getClientId());
    }
}
