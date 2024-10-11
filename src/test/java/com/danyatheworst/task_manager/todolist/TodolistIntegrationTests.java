package com.danyatheworst.task_manager.todolist;

import com.danyatheworst.task_manager.auth.dto.JwtUserDetailsDto;
import com.danyatheworst.task_manager.auth.jwt.JwtService;
import com.danyatheworst.task_manager.user.User;
import com.danyatheworst.task_manager.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
public class TodolistIntegrationTests {
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
    private MockMvc mockMvc;

    @Autowired
    private TodolistRepository todolistRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    private String accessToken;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final User user = new User(1L, "username", "password");

    @BeforeEach
    void setUp() {
        this.userRepository.save(this.user);
        JwtUserDetailsDto jwtUserDetailsDto = new JwtUserDetailsDto(
                this.user.getId(), this.user.getUsername(), this.user.getAuthorities()
        );
        this.accessToken = this.jwtService.generateAccessToken(jwtUserDetailsDto);
    }

    @AfterEach
    void tearDown() {
        this.todolistRepository.deleteAll();
        this.userRepository.deleteAll();
    }

    @Test
    void itShouldReturn201StatusCodeWhenTodolistWasCreated() throws Exception {
        String title = "todolist-test";
        RequestCreateTodolistDto payload = new RequestCreateTodolistDto(title);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/todolist")
                        .header("Authorization", "Bearer " + this.accessToken)
                        .content(this.objectMapper.writeValueAsString(payload))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        List<Todolist> todolists = this.todolistRepository.findAll();
        Assertions.assertEquals(1, todolists.size());
        Assertions.assertEquals(title, todolists.get(0).getTitle());
        Assertions.assertEquals(user.getId(), todolists.get(0).getUserId());
    }
}
