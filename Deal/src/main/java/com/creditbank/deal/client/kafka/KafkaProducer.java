package com.creditbank.deal.client.kafka;

import com.creditbank.deal.dto.dossier.EmailMessage;
import com.creditbank.deal.enums.EmailTheme;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.KafkaClient;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, EmailMessage> kafkaTemplate;


    public void sendMessage(String topic, String key, EmailMessage message) {
        kafkaTemplate.send(topic, key, message);
    }
}
