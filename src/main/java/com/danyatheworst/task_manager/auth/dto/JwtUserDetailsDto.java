package com.danyatheworst.task_manager.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
@AllArgsConstructor
public class JwtUserDetailsDto {
    private Long id;
    private String username;
    private Collection<? extends GrantedAuthority> authorities;
}
