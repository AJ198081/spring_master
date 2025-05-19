package dev.aj.kafka.email.service;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final Environment environment;

    public void sendEmail(String recipientEmail, String recipientName, String subject, String body) {

        javaMailSender.send(mimeMessage -> {
                    String smtpFrom = environment.getProperty("SMTP_FROM", "dummy@localhost.com");
                    String smtpName = environment.getProperty("SMTP_NAME", "Dummy Sender");
                    mimeMessage.setFrom(new InternetAddress(smtpFrom, smtpName));

                    InternetAddress to = new InternetAddress(recipientEmail);
                    to.setPersonal(recipientName);
                    mimeMessage.setRecipient(MimeMessage.RecipientType.TO, to);

                    mimeMessage.setSubject(subject);
                    mimeMessage.setText(body, "utf-8", "html");
                }
        );
    }

}
