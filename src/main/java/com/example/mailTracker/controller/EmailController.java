package com.example.mailTracker.controller;

import com.example.mailTracker.model.Email;
import com.example.mailTracker.service.EmailParser;
import com.example.mailTracker.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.Message;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@RestController
public class EmailController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailParser emailParser;

    @PostMapping("/send")
    public String sendEmail(@RequestParam String toEmail, @RequestParam String subject, @RequestParam String body) {
        emailService.sendEmail(toEmail, subject, body);
        return "Email sent successfully!";
    }

    @GetMapping("/getReplies")
    public Email getReplies(@RequestParam String subjectId) {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<Future<Email>> futures = new ArrayList<>();

        try {
            emailService.connectToStore();
            emailService.openFolder();

            Message[] messages = emailService.checkEmail();
            for (Message message : messages) {
                Callable<Email> task = () -> {
                    String subject = message.getSubject();
                    if (subject != null && subject.contains("[ID: " + subjectId + "]")) {
                        Email email = new Email();
                        email.setSubject(subject);
                        email.setContent(emailParser.parseContent(message));
                        return email;
                    }
                    return null;
                };
                futures.add(executor.submit(task));
            }

            for (Future<Email> future : futures) {
                Email email = future.get();
                if (email != null) {
                    executor.shutdownNow();
                    return email;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
        return null;
    }
}