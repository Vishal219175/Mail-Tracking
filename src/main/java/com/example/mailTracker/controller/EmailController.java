package com.example.mailTracker.controller;

import com.example.mailTracker.model.Email;
import com.example.mailTracker.service.EmailParser;
import com.example.mailTracker.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.mail.*;
import javax.mail.Message;
import javax.mail.search.SearchTerm;
import javax.mail.search.SubjectTerm;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * The Email Entity.
 *
 * @author rakesh.mahajan@nobrainsolutions.com
 * @version 1.0
 * @since 1.0
 */
@RestController
public class EmailController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailParser emailParser;

    private final ExecutorService executor;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
        this.executor = Executors.newCachedThreadPool();
    }


    @PostMapping("/send")
    public String sendEmail(@RequestParam String toEmail, @RequestParam String subject, @RequestParam String body) {
        emailService.sendEmail(toEmail, subject, body);
        return "Email sent successfully!";
    }

    @GetMapping("/getReplies")
    public CompletableFuture<Email> getReplies(@RequestParam String subjectId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                emailService.connectToStore();
                emailService.openFolder();

                SearchTerm searchTerm = new SubjectTerm("[ID: " + subjectId + "]");
                Message[] messages = emailService.searchEmails(searchTerm);

                for (Message message : messages) {
                    if (message.getSubject().contains("[ID: " + subjectId + "]")) {
                        Email email = new Email();
                        email.setSubject(message.getSubject());
                        email.setContent(emailParser.parseContent(message));
                        return email;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                emailService.closeFolder();
                emailService.disconnectStore();
            }
            return null;
        }, executor);
    }
}