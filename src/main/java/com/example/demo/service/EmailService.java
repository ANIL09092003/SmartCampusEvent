package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    public void sendRegistrationEmail(String to, String studentName, String eventName, int tickets, double amount) {
        if (mailSender == null) {
            System.out.println("[EMAIL SKIPPED] No mail config. Would send to: " + to);
            return;
        }
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(to);
            msg.setSubject("Registration Confirmed: " + eventName);
            msg.setText("Hi " + studentName + ",\n\n" +
                    "Your registration is confirmed!\n\n" +
                    "Event: " + eventName + "\n" +
                    "Tickets: " + tickets + "\n" +
                    "Total Amount: ₹" + amount + "\n\n" +
                    "Thank you,\nSmart Campus Event System");
            mailSender.send(msg);
        } catch (Exception e) {
            System.out.println("[EMAIL ERROR] " + e.getMessage());
        }
    }
}
