package com.danyatheworst.task_manager.auth;

import com.danyatheworst.task_manager.auth.dto.RequestSignUpDto;
import com.danyatheworst.task_manager.exceptions.EntityAlreadyExistsException;
import com.danyatheworst.task_manager.user.User;
import com.danyatheworst.task_manager.user.UserRepository;
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
public class SingUpIntegrationTests {
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
        String login = "user@gmail.com";
        RequestSignUpDto payload = new RequestSignUpDto(login, "password");

        //when
        User signedUpUser = this.registrationService.createUser(payload);

        //then
        Optional<User> user = this.userRepository.findByEmail(login);
        Assertions.assertTrue(user.isPresent());
        Assertions.assertEquals(user.get().getId(), signedUpUser.getId());
        Assertions.assertEquals(user.get().getUsername(), login);
        Assertions.assertEquals(this.userRepository.findAll().size(), 1);
    }

    @Test
    void itShouldThrowEntityAlreadyExistsExceptionWhenUserAlreadyExists() {
        //given
        String login = "user@gmail.com";
        RequestSignUpDto payload = new RequestSignUpDto(login, "password");
        this.userRepository.save(new User(login, "password"));

        //when and then
        Assertions.assertThrows(EntityAlreadyExistsException.class,
                () -> this.registrationService.createUser(payload));
    }
}
