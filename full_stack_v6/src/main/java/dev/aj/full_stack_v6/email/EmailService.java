package dev.aj.full_stack_v6.email;

import dev.aj.full_stack_v6.common.domain.events.MaliciousOperationEvent;

public interface EmailService {
    void sendEmail(String recipientEmail, String recipientName, String subject, String body);

    void on(MaliciousOperationEvent event);
}
