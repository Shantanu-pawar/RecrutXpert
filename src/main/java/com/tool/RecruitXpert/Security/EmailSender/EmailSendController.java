package com.tool.RecruitXpert.Security.EmailSender;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailSendController {
    @Autowired EmailService emailService;

    @PostMapping("/sendMail")
    public String sendMail(@RequestBody EmailDto email){
        return emailService.sendEmail(email);
    }

}
