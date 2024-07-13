package com.example.mailTracker.service;

import org.springframework.stereotype.Service;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;

@Service
public class EmailParser {

    public String parseContent(Message message) throws MessagingException, IOException {
        if (message.isMimeType("text/plain")) {
            return message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            return getTextFromMimeMultipart(mimeMultipart);
        }
        return "";
    }

    private String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws MessagingException, IOException {
        StringBuilder result = new StringBuilder();
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            Part part = mimeMultipart.getBodyPart(i);
            if (part.isMimeType("text/plain")) {
                result.append(part.getContent().toString());
            } else if (part.isMimeType("text/html")) {
                String html = (String) part.getContent();
                result.append(org.jsoup.Jsoup.parse(html).text());
            }
        }
        return result.toString();
    }
}
