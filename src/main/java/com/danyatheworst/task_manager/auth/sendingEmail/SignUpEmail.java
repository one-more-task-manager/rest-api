package com.danyatheworst.task_manager.auth.sendingEmail;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SignUpEmail {
    private String userId;
    private String email;
    private String title;
    private String body;
}