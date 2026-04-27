package com.creditbank.dossier.service;

import com.creditbank.dossier.dto.EmailMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmailService {

    public final JavaMailSender emailSender;

    public void sendEmail(EmailMessage message){
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        simpleMailMessage.setTo(message.getAddress());
        simpleMailMessage.setSubject(message.getTheme().toString());
        simpleMailMessage.setText(message.getText());

        emailSender.send(simpleMailMessage);
    }
}
