package de.betoffice.web.task;

import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message.RecipientType;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import org.springframework.stereotype.Component;

import de.winkler.betoffice.conf.BetofficeProperties;

@Component
public class MailTask {

    private final BetofficeProperties betofficeProperties;

    public MailTask(BetofficeProperties betofficeProperties) {
        this.betofficeProperties = betofficeProperties;
    }

    public void send() throws Exception {
        final String to = "gluehloch@googlemail.com";
        final String from = "betoffice@andre-winkler.de";
        final String username = betofficeProperties.getMailUsername();
        final String password = betofficeProperties.getMailPassword();
        final String host = betofficeProperties.getMailHost();
        final String port = betofficeProperties.getMailPort();

        Properties props = new Properties();
        props.put("mail.smtp.auth", Boolean.toString(betofficeProperties.getMailSmtpAuth()));
        props.put("mail.smtp.starttls.enable", Boolean.toString(betofficeProperties.getMailStartTlsEnable()));
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        final Authenticator authenticator = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new jakarta.mail.PasswordAuthentication(username, password);
            }
        };
        Session session = Session.getInstance(props, authenticator);

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(RecipientType.TO, InternetAddress.parse(to));
            message.setSubject("Here comes Jakarta Mail!");
            message.setText("Just discovered that Jakarta Mail is fun and easy to use");
            Transport.send(message);
            System.out.println("Email Message Sent Successfully");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
