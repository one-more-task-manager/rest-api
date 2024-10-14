package com.danyatheworst.task_manager.auth;

import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class SendingEmailService {

    private KafkaTemplate<Object, Object> template;

    public void sendEmail(@PathVariable String what) {
        this.template.send("topic1", new Foo1(what));
    }
}
