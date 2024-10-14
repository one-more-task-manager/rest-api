package com.danyatheworst.task_manager.auth;

import com.danyatheworst.task_manager.auth.dto.RequestSignUpDto;
import com.danyatheworst.task_manager.auth.sendingEmail.SendingEmailService;
import com.danyatheworst.task_manager.auth.sendingEmail.SignUpEvent;
import com.danyatheworst.task_manager.exceptions.EntityAlreadyExistsException;
import com.danyatheworst.task_manager.user.User;
import com.danyatheworst.task_manager.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RegistrationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SendingEmailService sendingEmailService;

    public User createUser(RequestSignUpDto payload) {
        try {
            String encodedPassword = this.passwordEncoder.encode(payload.getPassword());
            User user = new User(payload.getUsername(), encodedPassword);
            this.userRepository.save(user);
            return user;
        } catch (DataIntegrityViolationException e) {
            throw new EntityAlreadyExistsException("That username is taken. Try another");
        }
    }

    public void handleNewUser(RequestSignUpDto signUpDto) {
        User user = this.createUser(signUpDto);
        SignUpEvent event = new SignUpEvent(user.getId().toString(), "danyatheworst@gmail.com", user.getUsername());
        this.sendingEmailService.sendEmail(event);
    }
}