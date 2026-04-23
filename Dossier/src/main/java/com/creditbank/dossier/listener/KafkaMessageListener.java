package com.creditbank.dossier.listener;

import com.creditbank.dossier.dto.EmailMessage;
import com.creditbank.dossier.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaMessageListener {

    private final EmailService emailService;

    @KafkaListener(topics = {
            "finish-registration",
            "create-documents",
            "send-documents",
            "send-ses",
            "credit-issued",
            "statement-denied"
    })
    public void handleEmailMessages(EmailMessage message) {
        emailService.sendEmail(message);
    }
}
