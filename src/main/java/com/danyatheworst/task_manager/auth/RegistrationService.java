package com.danyatheworst.task_manager.auth;

import com.danyatheworst.task_manager.auth.dto.RequestSignUpDto;
import com.danyatheworst.task_manager.auth.sendingEmail.SendingEmailService;
import com.danyatheworst.task_manager.auth.sendingEmail.SignUpEmail;
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
            User user = new User(payload.getEmail(), encodedPassword);
            this.userRepository.save(user);
            return user;
        } catch (DataIntegrityViolationException e) {
            throw new EntityAlreadyExistsException("That email is taken. Try another");
        }
    }

    public void handleNewUser(RequestSignUpDto signUpDto) {
        this.createUser(signUpDto);
        SignUpEmail email = new SignUpEmail(
                signUpDto.getEmail(),
                "Welcome to our platform",
                "Hello " + signUpDto.getEmail() + ",\\n\\nThank you for signing up!\""
        );
        this.sendingEmailService.sendEmail(email);
    }
}