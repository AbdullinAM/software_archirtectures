/**
 * Created by kivi on 29.05.17.
 */

package com.spbpu.service;

import com.spbpu.config.ConfigReader;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.NamingException;
import java.io.IOException;
import java.util.Properties;

public class VerifyEmailService {

    String login;
    String name;
    String email;
    String password;

    public VerifyEmailService(String login_, String name_, String email_, String password_) {
        login = login_;
        name = name_;
        email = email_;
        password = password_;
    }

    public boolean verify() {
        if (!isValidEmailAddress()) return false;
        return sendEmail();
    }

    private boolean isValidEmailAddress() {
        try {
            new InternetAddress(email).validate();
        } catch (AddressException ex) {
            return false;
        }
        String hostname = email.split("@")[1];
        try {
            return EmailUtil.doMailServerLookup(hostname);
        } catch (NamingException e) {
            return false;
        }
    }

    private boolean sendEmail() {
        ConfigReader config;
        try {
            config = ConfigReader.getInstance();
        } catch (IOException e) {
            return false;
        }
        final String fromEmail = config.getEmailAccount();
        final String emailPassword = config.getEmailPassword();

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        //create Authenticator object to pass in Session.getInstance argument
        Authenticator auth = new Authenticator() {
            //override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, emailPassword);
            }
        };
        Session session = Session.getInstance(props, auth);

        StringBuilder sb = new StringBuilder();
        sb.append("You were registered at project management system.\n");
        sb.append("Login: " + login + "\n");
        sb.append("Name: " + name + "\n");
        sb.append("Password: " + password + "\n");

        EmailUtil.sendEmail(session, email,"Resistration at PMS", sb.toString());
        return true;
    }

    private class SMTPAuthenticator extends javax.mail.Authenticator {
        public PasswordAuthentication getPasswordAuthentication() {
            String username = "pmsbot.spbpu@gmail.com";
            String password = "pmsbotspbpu";
            return new PasswordAuthentication(username, password);
        }
    }
}
