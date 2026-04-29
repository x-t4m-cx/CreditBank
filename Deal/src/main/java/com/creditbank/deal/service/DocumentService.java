package com.creditbank.deal.service;

import com.creditbank.deal.client.kafka.KafkaProducer;
import com.creditbank.deal.config.property.EmailMessagesTextProperties;
import com.creditbank.deal.config.property.KafkaTopicProperties;
import com.creditbank.deal.dto.dossier.EmailMessage;
import com.creditbank.deal.entity.Client;
import com.creditbank.deal.entity.Statement;
import com.creditbank.deal.enums.ApplicationStatus;
import com.creditbank.deal.enums.ChangeType;
import com.creditbank.deal.enums.EmailTheme;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentService {
    private final ClientService clientService;
    private final StatementService statementService;
    private final KafkaProducer producer;
    private final KafkaTopicProperties topicProperties;
    private final EmailMessagesTextProperties textProperties;

    public void sendEmailRequest(String topic, UUID statementId, EmailTheme theme) {
        EmailMessage message = createEmailMessage(statementId, theme);
        producer.sendMessage(topic, statementId.toString(), message);
    }

    public void sendFinishRegistrationRequest(UUID statementId) {
        sendEmailRequest(topicProperties.getFinishRegistration(), statementId, EmailTheme.FINISH_REGISTRATION);
    }

    public void sendCreateDocumentRequest(UUID statementId) {
        sendEmailRequest(topicProperties.getCreateDocuments(), statementId, EmailTheme.CREATE_DOCUMENTS);
    }

    @Transactional
    public void sendSendDocumentRequest(UUID statementId) {
        Statement statement = statementService.getStatementById(statementId);
        statementService.setStatus(statement, ApplicationStatus.PREPARE_DOCUMENTS, ChangeType.MANUAL);
        statementService.updateStatement(statement);

        sendEmailRequest(topicProperties.getSendDocuments(), statementId, EmailTheme.SEND_DOCUMENTS);
    }

    @Transactional
    public void sendSignDocumentRequest(UUID statementId) {
        Statement statement = statementService.getStatementById(statementId);
        statementService.updateStatementWithSesCode(statement);
        sendEmailRequest(topicProperties.getSendSes(), statementId, EmailTheme.SEND_SES);
    }

    public void sendStatementDenied(UUID statementId){
        sendEmailRequest(topicProperties.getStatementDenied(), statementId, EmailTheme.STATEMENT_DENIED);
    }

    @Transactional
    public void sendCreditIssueCredit(UUID statementId) {
        Statement statement = statementService.getStatementById(statementId);
        statementService.setStatus(statement, ApplicationStatus.DOCUMENT_SIGNED, ChangeType.MANUAL);
        statementService.setStatus(statement, ApplicationStatus.CREDIT_ISSUED, ChangeType.MANUAL);
        statementService.updateStatement(statement);
        sendEmailRequest(topicProperties.getCreditIssued(), statementId, EmailTheme.CREDIT_ISSUED);
    }

    private EmailMessage createEmailMessage(UUID statementId, EmailTheme theme) {
        Client client = clientService.getClientByStatementId(statementId);

        String text = textProperties.getMessages().get(theme.toString());
        if (theme == EmailTheme.SEND_SES) {
            String code = client.getStatement().getSesCode();
            text = text.replace("{code}", code);
        }
        return EmailMessage.builder()
                .address(client.getEmail())
                .theme(theme)
                .text(text)
                .statementId(statementId)
                .build();
    }
}