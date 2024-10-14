package com.danyatheworst.task_manager.auth.sendingEmail;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SignUpEvent {
    private String userId;
    private String email;
    private String username;
}