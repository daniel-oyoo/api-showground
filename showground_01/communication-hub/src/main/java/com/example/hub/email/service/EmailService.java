 
package com.example.hub.email.service;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.hub.email.dto.EmailRequest;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public String send(EmailRequest req) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(
            req.getTo()
        );
        msg.setSubject(
            req.getSubject()
        );
        msg.setText(
            req.getBody()
        );
        try{
            mailSender.send(msg);
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return "Email sent to " + req.getTo();
    }
}