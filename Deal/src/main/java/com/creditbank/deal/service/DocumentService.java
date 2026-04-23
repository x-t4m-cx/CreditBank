package com.creditbank.deal.service;

import com.creditbank.deal.client.kafka.KafkaProducer;
import com.creditbank.deal.dto.dossier.EmailMessage;
import com.creditbank.deal.entity.Client;
import com.creditbank.deal.enums.EmailTheme;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentService {
    private final ClientService clientService;
    private final KafkaProducer producer;

    public void sendEmailRequest(UUID statementId, EmailTheme theme) {
        EmailMessage message = createEmailMessage(statementId, theme);
        producer.sendMessage(message, statementId);
    }

    public void sendFinishRegistrationRequest(UUID statementId) {
        sendEmailRequest(statementId, EmailTheme.FINISH_REGISTRATION);
    }

    public void sendCreateDocumentRequest(UUID statementId) {
        sendEmailRequest(statementId, EmailTheme.CREATE_DOCUMENTS);
    }

    public void sendSendDocumentRequest(UUID statementId) {
        sendEmailRequest(statementId, EmailTheme.SEND_DOCUMENTS);
    }

    public void sendSignDocumentRequest(UUID statementId) {
        sendEmailRequest(statementId, EmailTheme.SEND_SES);
    }

    public void sendCreditDenied(UUID statementId){
        sendEmailRequest(statementId, EmailTheme.STATEMENT_DENIED);
    }
    public void sendCreditIssueCredit(UUID statementId) {
        sendEmailRequest(statementId, EmailTheme.CREDIT_ISSUED);
    }

    private EmailMessage createEmailMessage(UUID statementId, EmailTheme theme) {
        Client client = clientService.getClientByStatementId(statementId);
        return EmailMessage.builder()
                .address(client.getEmail())
                .theme(theme)
                .statementId(statementId)
                .build();
    }
}