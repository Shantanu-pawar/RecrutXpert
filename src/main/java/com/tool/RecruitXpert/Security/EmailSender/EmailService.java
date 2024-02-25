package com.tool.RecruitXpert.Security.EmailSender;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public String sendEmail(EmailDto email){
        try{
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(sender);
            mailMessage.setTo(email.getRecipient());
            mailMessage.setSubject(email.getSubject());
            mailMessage.setText(email.getMessage());

            javaMailSender.send((mailMessage));
            return "email send successfully!";
        }catch (Exception e){
            return "email sending failed";
        }
    }

}
