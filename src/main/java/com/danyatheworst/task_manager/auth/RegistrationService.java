package com.danyatheworst.task_manager.auth;

import com.danyatheworst.task_manager.auth.dto.RequestSignUpDto;
import com.danyatheworst.task_manager.exceptions.EntityAlreadyExistsException;
import com.danyatheworst.task_manager.user.UserRepository;
import com.danyatheworst.task_manager.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RegistrationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Long createUser(RequestSignUpDto payload) {
        try {
            String encodedPassword = this.passwordEncoder.encode(payload.getPassword());
            User user = new User(payload.getUsername(), encodedPassword);
            this.userRepository.save(user);
            return user.getId();
        } catch (DataIntegrityViolationException e) {
            throw new EntityAlreadyExistsException("That username is taken. Try another");
        }
    }

    public void handleNewUser(RequestSignUpDto signUpDto) {
        Long userId = this.createUser(signUpDto);
        //sender email function
    }
}