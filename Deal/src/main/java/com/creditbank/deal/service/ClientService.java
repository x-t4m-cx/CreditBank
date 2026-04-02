package com.creditbank.deal.service;

import com.creditbank.deal.dto.request.FinishRegistrationRequestDto;
import com.creditbank.deal.dto.request.LoanStatementRequestDto;
import com.creditbank.deal.entity.Client;
import com.creditbank.deal.entity.Statement;
import com.creditbank.deal.mapper.ClientMapper;
import com.creditbank.deal.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientService {
    private final ClientMapper clientMapper;
    private final ClientRepository repository;

    public Client createClient(LoanStatementRequestDto request) {
        Client client = clientMapper.toEntity(request);

        Client savedClient = repository.save(client);
        log.debug("Client created - id: {}", savedClient.getClientId());
        return savedClient;
    }

    public void updateClient(Statement statement, FinishRegistrationRequestDto request) {
        Client client = statement.getClient();
        clientMapper.updateEntity(client, request);

        Client updatedClient = repository.save(client);
        statement.setClient(updatedClient);
        log.debug("Client updated - id: {}", updatedClient.getClientId());
    }
}
