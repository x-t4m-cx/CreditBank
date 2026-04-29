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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private ClientService clientService;
    @Mock
    private StatementService statementService;
    @Mock
    private KafkaProducer producer;
    @Mock
    private KafkaTopicProperties topicProperties;
    @Mock
    private EmailMessagesTextProperties textProperties;
    @InjectMocks
    private DocumentService documentService;

    @Test
    void shouldSendFinishRegistrationRequestWhenValidStatementIdProvided() {
        UUID statementId = UUID.randomUUID();
        String topicName = "finish-registration";
        String email = "client@example.com";
        String textMessage = "Требуется завершение регистрации";

        Client client = new Client();
        client.setEmail(email);

        when(topicProperties.getFinishRegistration()).thenReturn(topicName);
        when(clientService.getClientByStatementId(statementId)).thenReturn(client);
        when(textProperties.getMessages()).thenReturn(Map.of(EmailTheme.FINISH_REGISTRATION.toString(), textMessage));

        documentService.sendFinishRegistrationRequest(statementId);

        verify(topicProperties).getFinishRegistration();
        verify(clientService).getClientByStatementId(statementId);
        verify(producer).sendMessage(eq(topicName), eq(statementId.toString()), any(EmailMessage.class));
    }

    @Test
    void shouldSendCreateDocumentRequestWhenValidStatementIdProvided() {
        UUID statementId = UUID.randomUUID();
        String topicName = "create-documents";
        String email = "client@example.com";
        String textMessage = "Документы сформированы";

        Client client = new Client();
        client.setEmail(email);

        when(topicProperties.getCreateDocuments()).thenReturn(topicName);
        when(clientService.getClientByStatementId(statementId)).thenReturn(client);
        when(textProperties.getMessages()).thenReturn(Map.of(EmailTheme.CREATE_DOCUMENTS.toString(), textMessage));

        documentService.sendCreateDocumentRequest(statementId);

        verify(topicProperties).getCreateDocuments();
        verify(clientService).getClientByStatementId(statementId);
        verify(producer).sendMessage(eq(topicName), eq(statementId.toString()), any(EmailMessage.class));
    }

    @Test
    void shouldSendSendDocumentRequestWhenValidStatementIdProvided() {
        UUID statementId = UUID.randomUUID();
        String topicName = "send-documents";
        String email = "client@example.com";
        String textMessage = "Документы отправлены на подпись";

        Statement statement = new Statement();
        statement.setStatementId(statementId);

        Client client = new Client();
        client.setEmail(email);
        client.setStatement(statement);

        when(topicProperties.getSendDocuments()).thenReturn(topicName);
        when(statementService.getStatementById(statementId)).thenReturn(statement);
        when(clientService.getClientByStatementId(statementId)).thenReturn(client);
        when(textProperties.getMessages()).thenReturn(Map.of(EmailTheme.SEND_DOCUMENTS.toString(), textMessage));

        documentService.sendSendDocumentRequest(statementId);

        verify(statementService).getStatementById(statementId);
        verify(statementService).setStatus(statement, ApplicationStatus.PREPARE_DOCUMENTS, ChangeType.MANUAL);
        verify(statementService).updateStatement(statement);
        verify(clientService).getClientByStatementId(statementId);
        verify(producer).sendMessage(eq(topicName), eq(statementId.toString()), any(EmailMessage.class));
    }

    @Test
    void shouldSendSignDocumentRequestWhenValidStatementIdProvided() {
        UUID statementId = UUID.randomUUID();
        String topicName = "send-ses";
        String email = "client@example.com";
        String sesCode = "123456";
        String textTemplate = "Код подтверждения отправлен. Ваш код - {code}";

        Statement statement = new Statement();
        statement.setStatementId(statementId);
        statement.setSesCode(sesCode);

        Client client = new Client();
        client.setEmail(email);
        client.setStatement(statement);

        when(topicProperties.getSendSes()).thenReturn(topicName);
        when(statementService.getStatementById(statementId)).thenReturn(statement);
        when(clientService.getClientByStatementId(statementId)).thenReturn(client);
        when(textProperties.getMessages()).thenReturn(Map.of(EmailTheme.SEND_SES.toString(), textTemplate));

        documentService.sendSignDocumentRequest(statementId);

        verify(statementService).getStatementById(statementId);
        verify(statementService).updateStatementWithSesCode(statement);
        verify(clientService).getClientByStatementId(statementId);
        verify(producer).sendMessage(eq(topicName), eq(statementId.toString()), any(EmailMessage.class));
    }

    @Test
    void shouldSendStatementDeniedWhenValidStatementIdProvided() {
        UUID statementId = UUID.randomUUID();
        String topicName = "statement-denied";
        String email = "client@example.com";
        String textMessage = "В заявке отказано";

        Client client = new Client();
        client.setEmail(email);

        when(topicProperties.getStatementDenied()).thenReturn(topicName);
        when(clientService.getClientByStatementId(statementId)).thenReturn(client);
        when(textProperties.getMessages()).thenReturn(Map.of(EmailTheme.STATEMENT_DENIED.toString(), textMessage));

        documentService.sendStatementDenied(statementId);

        verify(topicProperties).getStatementDenied();
        verify(clientService).getClientByStatementId(statementId);
        verify(producer).sendMessage(eq(topicName), eq(statementId.toString()), any(EmailMessage.class));
    }

    @Test
    void shouldSendCreditIssueCreditWhenValidStatementIdProvided() {
        UUID statementId = UUID.randomUUID();
        String topicName = "credit-issued";
        String email = "client@example.com";
        String textMessage = "Кредит успешно выдан";

        Statement statement = new Statement();
        statement.setStatementId(statementId);

        Client client = new Client();
        client.setEmail(email);
        client.setStatement(statement);

        when(topicProperties.getCreditIssued()).thenReturn(topicName);
        when(statementService.getStatementById(statementId)).thenReturn(statement);
        when(clientService.getClientByStatementId(statementId)).thenReturn(client);
        when(textProperties.getMessages()).thenReturn(Map.of(EmailTheme.CREDIT_ISSUED.toString(), textMessage));

        documentService.sendCreditIssueCredit(statementId);

        verify(statementService).getStatementById(statementId);
        verify(statementService).setStatus(statement, ApplicationStatus.DOCUMENT_SIGNED, ChangeType.MANUAL);
        verify(statementService).setStatus(statement, ApplicationStatus.CREDIT_ISSUED, ChangeType.MANUAL);
        verify(statementService).updateStatement(statement);
        verify(clientService).getClientByStatementId(statementId);
        verify(producer).sendMessage(eq(topicName), eq(statementId.toString()), any(EmailMessage.class));
    }

    @Test
    void shouldSendEmailRequestWithCorrectEmailDataWhenThemeIsNotSes() {
        UUID statementId = UUID.randomUUID();
        String topic = "send-documents";
        EmailTheme theme = EmailTheme.SEND_DOCUMENTS;
        String email = "client@example.com";
        String textMessage = "Документы отправлены на подпись";

        Statement statement = new Statement();
        statement.setStatementId(statementId);

        Client client = new Client();
        client.setEmail(email);
        client.setStatement(statement);

        when(statementService.getStatementById(statementId)).thenReturn(statement);
        when(clientService.getClientByStatementId(statementId)).thenReturn(client);
        when(textProperties.getMessages()).thenReturn(Map.of(theme.toString(), textMessage));
        when(topicProperties.getSendDocuments()).thenReturn(topic);

        documentService.sendSendDocumentRequest(statementId);

        ArgumentCaptor<EmailMessage> messageCaptor = ArgumentCaptor.forClass(EmailMessage.class);
        verify(producer).sendMessage(eq(topic), eq(statementId.toString()), messageCaptor.capture());

        EmailMessage capturedMessage = messageCaptor.getValue();
        assertEquals(email, capturedMessage.getAddress());
        assertEquals(theme, capturedMessage.getTheme());
        assertEquals(textMessage, capturedMessage.getText());
        assertEquals(statementId, capturedMessage.getStatementId());
    }

    @Test
    void shouldSendEmailRequestWithSesCodeWhenThemeIsSendSes() {
        UUID statementId = UUID.randomUUID();
        String topic = "send-ses";
        EmailTheme theme = EmailTheme.SEND_SES;
        String email = "client@example.com";
        String sesCode = "123456";
        String textTemplate = "Код подтверждения отправлен. Ваш код - {code}";
        String expectedText = "Код подтверждения отправлен. Ваш код - 123456";

        Statement statement = new Statement();
        statement.setStatementId(statementId);
        statement.setSesCode(sesCode);

        Client client = new Client();
        client.setEmail(email);
        client.setStatement(statement);

        when(statementService.getStatementById(statementId)).thenReturn(statement);
        when(clientService.getClientByStatementId(statementId)).thenReturn(client);
        when(textProperties.getMessages()).thenReturn(Map.of(theme.toString(), textTemplate));
        when(topicProperties.getSendSes()).thenReturn(topic);

        documentService.sendSignDocumentRequest(statementId);

        ArgumentCaptor<EmailMessage> messageCaptor = ArgumentCaptor.forClass(EmailMessage.class);
        verify(producer).sendMessage(eq(topic), eq(statementId.toString()), messageCaptor.capture());

        EmailMessage capturedMessage = messageCaptor.getValue();
        assertEquals(email, capturedMessage.getAddress());
        assertEquals(theme, capturedMessage.getTheme());
        assertEquals(expectedText, capturedMessage.getText());
        assertEquals(statementId, capturedMessage.getStatementId());
        assertTrue(capturedMessage.getText().contains(sesCode));
        assertFalse(capturedMessage.getText().contains("{code}"));
    }

    @Test
    void shouldThrowExceptionWhenClientNotFoundForStatement() {
        UUID statementId = UUID.randomUUID();

        when(statementService.getStatementById(statementId))
                .thenThrow(new RuntimeException("Statement not found"));

        assertThrows(RuntimeException.class, () ->
                documentService.sendSendDocumentRequest(statementId));

        verify(producer, never()).sendMessage(any(), any(), any());
    }

    @Test
    void shouldHandleMultipleStatusUpdatesInCreditIssue() {
        UUID statementId = UUID.randomUUID();
        String topicName = "credit-issued";
        String email = "client@example.com";
        String textMessage = "Кредит успешно выдан";

        Statement statement = new Statement();
        statement.setStatementId(statementId);

        Client client = new Client();
        client.setEmail(email);
        client.setStatement(statement);

        when(topicProperties.getCreditIssued()).thenReturn(topicName);
        when(statementService.getStatementById(statementId)).thenReturn(statement);
        when(clientService.getClientByStatementId(statementId)).thenReturn(client);
        when(textProperties.getMessages()).thenReturn(Map.of(EmailTheme.CREDIT_ISSUED.toString(), textMessage));

        documentService.sendCreditIssueCredit(statementId);

        verify(statementService).setStatus(statement, ApplicationStatus.DOCUMENT_SIGNED, ChangeType.MANUAL);
        verify(statementService).setStatus(statement, ApplicationStatus.CREDIT_ISSUED, ChangeType.MANUAL);
        verify(statementService, times(1)).updateStatement(statement);
    }

    @Test
    void shouldVerifyAllKafkaTopicsAreConfigured() {
        UUID statementId = UUID.randomUUID();
        String email = "client@example.com";

        Client client = new Client();
        client.setEmail(email);

        Statement statement = new Statement();
        statement.setStatementId(statementId);
        statement.setSesCode("123456");
        client.setStatement(statement);

        Map<String, String> messageMap = Map.of(
                "FINISH_REGISTRATION", "Требуется завершение регистрации",
                "CREATE_DOCUMENTS", "Документы сформированы",
                "SEND_DOCUMENTS", "Документы отправлены на подпись",
                "SEND_SES", "Код подтверждения отправлен. Ваш код - {code}",
                "CREDIT_ISSUED", "Кредит успешно выдан",
                "STATEMENT_DENIED", "В заявке отказано"
        );

        when(topicProperties.getFinishRegistration()).thenReturn("finish-registration");
        when(topicProperties.getCreateDocuments()).thenReturn("create-documents");
        when(topicProperties.getSendDocuments()).thenReturn("send-documents");
        when(topicProperties.getSendSes()).thenReturn("send-ses");
        when(topicProperties.getCreditIssued()).thenReturn("credit-issued");
        when(topicProperties.getStatementDenied()).thenReturn("statement-denied");

        when(clientService.getClientByStatementId(any(UUID.class))).thenReturn(client);
        when(textProperties.getMessages()).thenReturn(messageMap);
        when(statementService.getStatementById(any(UUID.class))).thenReturn(statement);

        documentService.sendFinishRegistrationRequest(statementId);
        verify(producer).sendMessage(eq("finish-registration"), eq(statementId.toString()), any());

        documentService.sendCreateDocumentRequest(statementId);
        verify(producer).sendMessage(eq("create-documents"), eq(statementId.toString()), any());

        documentService.sendSendDocumentRequest(statementId);
        verify(producer).sendMessage(eq("send-documents"), eq(statementId.toString()), any());

        documentService.sendSignDocumentRequest(statementId);
        verify(producer).sendMessage(eq("send-ses"), eq(statementId.toString()), any());

        documentService.sendCreditIssueCredit(statementId);
        verify(producer).sendMessage(eq("credit-issued"), eq(statementId.toString()), any());

        documentService.sendStatementDenied(statementId);
        verify(producer).sendMessage(eq("statement-denied"), eq(statementId.toString()), any());
    }

    @Test
    void shouldHandleNullSesCodeGracefully() {
        UUID statementId = UUID.randomUUID();
        String topic = "send-ses";
        EmailTheme theme = EmailTheme.SEND_SES;
        String email = "test@example.com";
        String sesCode = "1234";
        String textTemplate = "Код подтверждения отправлен. Ваш код - {code}";

        Statement statement = new Statement();
        statement.setStatementId(statementId);
        statement.setSesCode(sesCode);

        Client client = new Client();
        client.setEmail(email);
        client.setStatement(statement);

        when(statementService.getStatementById(statementId)).thenReturn(statement);
        when(clientService.getClientByStatementId(statementId)).thenReturn(client);
        when(textProperties.getMessages()).thenReturn(Map.of(theme.toString(), textTemplate));
        when(topicProperties.getSendSes()).thenReturn(topic);

        documentService.sendSignDocumentRequest(statementId);

        ArgumentCaptor<EmailMessage> messageCaptor = ArgumentCaptor.forClass(EmailMessage.class);
        verify(producer).sendMessage(eq(topic), eq(statementId.toString()), messageCaptor.capture());

        assertEquals("Код подтверждения отправлен. Ваш код - 1234", messageCaptor.getValue().getText());
    }
}