package com.danyatheworst.task_manager.auth.sendingEmail;

import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class SendingEmailService {
    private final KafkaTemplate<String, SignUpEvent> template;
  
    public void sendEmail(SignUpEvent signUpEvent) {
        this.template.send("sign-up", signUpEvent);
    }
}