package com.creditbank.deal.config.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "kafka.topics")
public class KafkaTopicProperties {
    private String finishRegistration;
    private String createDocuments;
    private String sendDocuments;
    private String sendSes;
    private String creditIssued;
    private String statementDenied;
}
