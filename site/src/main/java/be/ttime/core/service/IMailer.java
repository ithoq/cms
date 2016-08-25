package be.ttime.core.service;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

public interface IMailer {

    void sendMail(String to, String subject, String body) throws MessagingException, UnsupportedEncodingException;
}
