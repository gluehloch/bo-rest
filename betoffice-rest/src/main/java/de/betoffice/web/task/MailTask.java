package de.betoffice.web.task;

import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message.RecipientType;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import org.springframework.stereotype.Component;

import de.winkler.betoffice.conf.BetofficeProperties;

@Component
public class MailTask {

    private final Authenticator authenticator;
    private final Properties mailProperties;
    private final boolean mailEnabled;

    public MailTask(BetofficeProperties betofficeProperties) {
        this.mailEnabled = betofficeProperties.isMailEnabled();
        this.authenticator = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new jakarta.mail.PasswordAuthentication(
                        betofficeProperties.getMailUsername(),
                        betofficeProperties.getMailPassword());
            }
        };

        mailProperties = new Properties();
        mailProperties.put("mail.smtp.auth", Boolean.toString(betofficeProperties.getMailSmtpAuth()));
        mailProperties.put("mail.smtp.starttls.enable", Boolean.toString(betofficeProperties.getMailStartTlsEnable()));
        mailProperties.put("mail.smtp.host", betofficeProperties.getMailHost());
        mailProperties.put("mail.smtp.port", betofficeProperties.getMailPort());
    }

    public void send(String from, String to, String subject, String message) throws Exception {
        if (mailEnabled) {
            Session session = Session.getInstance(mailProperties, authenticator);
            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(from));
            mimeMessage.setRecipients(RecipientType.TO, InternetAddress.parse(to));
            mimeMessage.setSubject(subject);
            mimeMessage.setText(message);
            Transport.send(mimeMessage);
        }
    }

}
