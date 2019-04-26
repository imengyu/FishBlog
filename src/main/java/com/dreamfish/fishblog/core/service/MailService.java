package com.dreamfish.fishblog.core.service;

import org.springframework.mail.MailException;

import javax.mail.MessagingException;

public interface MailService {

    void sendSimpleMail(String to, String subject, String content) throws MailException;
    void sendInlineResourceMail(String to, String subject, String content, String rscPath, String rscId) throws MessagingException;
    void sendHtmlMail(String to, String subject, String content) throws MessagingException;
}
