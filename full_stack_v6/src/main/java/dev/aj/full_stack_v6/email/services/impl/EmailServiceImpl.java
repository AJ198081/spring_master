package dev.aj.full_stack_v6.email.services.impl;

import dev.aj.full_stack_v6.common.domain.events.MaliciousOperationEvent;
import dev.aj.full_stack_v6.common.domain.events.OrderPlacedEvent;
import dev.aj.full_stack_v6.common.domain.events.dto.ShippingDetails;
import dev.aj.full_stack_v6.email.EmailService;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;
    private final Environment environment;

    @Override
    public void sendEmail(String recipientEmail, String recipientName, String subject, String body) {
        log.info("Sending {} email to {} ", subject, recipientEmail);

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

    @Override
    @ApplicationModuleListener
    public void on(MaliciousOperationEvent event) {
        this.sendEmail(
                event.email(),
                event.name(),
                event.message(),
                toHtml(event.name(), event.message(), event.timestamp())
        );
    }

    @Override
    @ApplicationModuleListener
    public void on(OrderPlacedEvent orderPlacedEvent) {
        this.sendEmail(
                orderPlacedEvent.shippingDetails().email(),
                orderPlacedEvent.firstName(),
                "Order id: %s successfully placed".formatted(orderPlacedEvent.orderId()),
                toOrderCreatedHtml(orderPlacedEvent.firstName(), orderPlacedEvent.orderId(), orderPlacedEvent.shippingDetails(), orderPlacedEvent.orderTotal())
        );
    }

    private String toHtml(String recipientName, String message, Instant timeOfAttempt) {
        return """
                <html>
                  <body style="font-family: Arial, sans-serif; color: #333;">
                    <h2>Hello %s,</h2>
                    <p>We have noticed a malicious operation being attempted at %s using your credentials!</p>
                    <p><strong>Error Message is:</strong> %s.</p>
                    <p><strong>As a precaution:</strong></p>
                    <ul>
                      <li>We have logged you out from the session, and</li>
                      <li>Have disabled your account</li>
                    </ul>
                    <p>If you believe this was a mistake, please emil <a href="mailto:full_stack_v6_support@gmail.com">Admin Support</a></p>
                    <p>Best regards,<br/>The Admin Team @Full_Stack_V6</p>
                  </body>
                </html>
                """.formatted(recipientName, ZonedDateTime.ofInstant(timeOfAttempt, ZoneId.systemDefault()), message);
    }

    private String toOrderCreatedHtml(String recipientName, String orderId, ShippingDetails shippingDetails, BigDecimal orderTotal) {
        return """
                <html>
                  <body style="font-family: Arial, sans-serif; color: #333;">
                    <h2>Hello %s,</h2>
                    <p>Thank you for your order! We're pleased to confirm that your order has been successfully placed.</p>
                    <div style="margin: 20px 0; padding: 15px; border: 1px solid #ddd; border-radius: 5px;">
                      <h3 style="color: #2c5282;">Order Details</h3>
                      <p><strong>Order ID:</strong> %s</p>
                      <p><strong>Order Total:</strong> $%s</p>
                      <h3 style="color: #2c5282; margin-top: 20px;">Shipping Details</h3>
                      <p>%s</p>
                    </div>
                    <p>We will notify you once your order has been shipped.</p>
                    <p>If you have any questions, please contact our <a href="mailto:full_stack_v6_support@gmail.com">Customer Support</a></p>
                    <p>Best regards,<br/>The Full_Stack_V6 Team</p>
                  </body>
                </html>
                """.formatted(recipientName,
                orderId,
                orderTotal,
                shippingDetails.toString()
                        .replace("ShippingDetails", "")
                        .replace("shippingAddress=", ""));
    }
}
