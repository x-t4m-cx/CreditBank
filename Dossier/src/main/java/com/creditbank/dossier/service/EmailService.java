package com.creditbank.dossier.service;

import com.creditbank.dossier.dto.EmailMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    public void sendEmail(EmailMessage message){
        log.info("message received:{}", message);
    }
}
