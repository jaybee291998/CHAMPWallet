package com.cwallet.champwallet.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.internet.MimeMessage;

@Component
public class EmailServiceImpl implements EmailService {
    @Autowired
    private JavaMailSender emailSender;
    public void sendSimpleMessage(String to, String subject, String text) throws SendFailedException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }

    public void sendMIMEMessage(String to, String subject, String text) throws SendFailedException {
        MimeMessage mimeMessage = emailSender.createMimeMessage();
        try {
            mimeMessage.setFrom("noreply@champwallet.com");
            mimeMessage.setRecipients(MimeMessage.RecipientType.TO, to);
            mimeMessage.setSubject(subject);
            mimeMessage.setText(text);

            mimeMessage.setContent(text, "text/html; charset=utf-8");
            emailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new SendFailedException("Failed to send email", e);
        }
    }
}
