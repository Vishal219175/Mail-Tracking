package com.example.mailTracker.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.UUID;

@Service
public class EmailService {

    @Value("${mail.smtp.host}")
    private String host;

    @Value("${mail.smtp.user}")
    private String user;

    @Value("${mail.smtp.password}")
    private String password;

    private Store store;
    private Folder emailFolder;

    public void sendEmail(String toEmail, String subject, String body) {
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });

        try {
            String uniqueId = UUID.randomUUID().toString();
            String modifiedSubject = subject + " [ID: " + uniqueId + "]";

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(user));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(modifiedSubject);
            message.setText(body);

            Transport.send(message);
            System.out.println("Email sent successfully!");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public void connectToStore() throws MessagingException {
        if (store == null || !store.isConnected()) {
            Properties properties = new Properties();
            properties.put("mail.store.protocol", "imaps");
            Session emailSession = Session.getDefaultInstance(properties);
            store = emailSession.getStore();
            store.connect("imap.gmail.com", user, password);
        }
    }

    public void openFolder() throws MessagingException {
        if (emailFolder == null || !emailFolder.isOpen()) {
            emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);
        }
    }

    public Message[] checkEmail() {
        try {
            connectToStore();
            openFolder();
            Message[] messages = emailFolder.getMessages();
            return messages;
        } catch (MessagingException e) {
            e.printStackTrace();
            return new Message[0];
        }
    }
}

