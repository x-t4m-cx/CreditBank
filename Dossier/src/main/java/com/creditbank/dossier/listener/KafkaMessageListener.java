package com.creditbank.dossier.listener;

import com.creditbank.dossier.config.KafkaTopicsProperties;
import com.creditbank.dossier.dto.EmailMessage;
import com.creditbank.dossier.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaMessageListener {

    private final EmailService emailService;
    private final KafkaTopicsProperties topicsProperties;
    @KafkaListener(topics = "#{@kafkaTopicsProperties.topics.toArray(new String[0])}")
    public void handleEmailMessages(EmailMessage message) {
        emailService.sendEmail(message);
    }
}
