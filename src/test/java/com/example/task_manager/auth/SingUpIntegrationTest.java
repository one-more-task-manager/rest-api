package com.example.task_manager.auth;

import com.example.task_manager.auth.dto.RequestSignUpDto;
import com.example.task_manager.exceptions.EntityAlreadyExistsException;
import com.example.task_manager.user.User;
import com.example.task_manager.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

@Testcontainers
@SpringBootTest
public class SingUpIntegrationTest {
    @Container
    private static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:14-alpine")
                    .withDatabaseName("database-test")
                    .withUsername("username-test")
                    .withPassword("password-test");

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RegistrationService registrationService;

    @AfterEach
    public void cleanUserTable() {
        this.userRepository.deleteAll();
    }

    @Test
    void itShouldInsertUserIntoDatabase() {
        //given
        String login = "user";
        RequestSignUpDto signUpDto = new RequestSignUpDto(login, "password");

        //when
        Long userId = this.registrationService.createUser(signUpDto);

        //then
        Optional<User> user = this.userRepository.findByUsername(login);
        Assertions.assertTrue(user.isPresent());
        Assertions.assertEquals(user.get().getId(), userId);
        Assertions.assertEquals(user.get().getUsername(), login);
        Assertions.assertEquals(this.userRepository.findAll().size(), 1);
    }

    @Test
    void itShouldReturn409StatusCodeWhenUserAlreadyExists() {
        //given
        String login = "user";
        RequestSignUpDto signUpDto = new RequestSignUpDto(login, "password");
        this.userRepository.save(new User(login, "password"));

        //when and then
        Assertions.assertThrows(EntityAlreadyExistsException.class,
                () -> this.registrationService.createUser(signUpDto));
    }
}
