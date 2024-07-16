package com.example.mailTracker.model;
/**
 * The Email Entity.
 *
 * @author rakesh.mahajan@nobrainsolutions.com
 * @version 1.0
 * @since 1.0
 */
public class Email {
    private String subject;
    private String content;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
