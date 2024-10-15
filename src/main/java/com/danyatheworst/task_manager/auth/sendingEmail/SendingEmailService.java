package com.danyatheworst.task_manager.auth.sendingEmail;

import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class SendingEmailService {
    private final KafkaTemplate<String, RegisterEmail> template;

    public void sendEmail(RegisterEmail registerEmail) {
        this.template.send("EMAIL_SENDING_TASKS", registerEmail);
    }
}