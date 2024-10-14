package com.danyatheworst.task_manager.auth.sendingEmail;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SignUpEvent {
    private String userId;
    private String email;
    private String username;
}