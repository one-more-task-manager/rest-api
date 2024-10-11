package com.danyatheworst.task_manager.task;

import com.danyatheworst.task_manager.auth.dto.JwtUserDetailsDto;
import com.danyatheworst.task_manager.auth.jwt.JwtService;
import com.danyatheworst.task_manager.todolist.Todolist;
import com.danyatheworst.task_manager.todolist.TodolistRepository;
import com.danyatheworst.task_manager.user.User;
import com.danyatheworst.task_manager.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
public class TaskIntegrationTests {
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
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String accessToken;
    private User user;
    private Todolist todolist;

    @BeforeEach
    void setUp() {
        this.user = this.userRepository.save(new User("username", "password"));
        this.todolist = this.todolistRepository.save(new Todolist("todolist", this.user.getId()));
        JwtUserDetailsDto jwtUserDetailsDto = new JwtUserDetailsDto(
                this.user.getId(), this.user.getUsername(), this.user.getAuthorities()
        );
        this.accessToken = this.jwtService.generateAccessToken(jwtUserDetailsDto);
    }

    @AfterEach
    void tearDown() {
        this.taskRepository.deleteAll();
        this.todolistRepository.deleteAll();
        this.userRepository.deleteAll();
    }

    @Test
    void itShouldReturn201StatusCodeWhenTodolistWasCreated() throws Exception {
        String taskTitle = "task-test";
        Long todolistId = this.todolist.getId();
        RequestCreateTaskDto payload = new RequestCreateTaskDto(taskTitle);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/todolists/{todolistId}/tasks", todolistId)
                        .header("Authorization", "Bearer " + this.accessToken)
                        .content(this.objectMapper.writeValueAsString(payload))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        List<Task> tasks = this.taskRepository.findAll();
        Assertions.assertEquals(1, tasks.size());
        Assertions.assertEquals(taskTitle, tasks.get(0).getTitle());
        Assertions.assertEquals(todolistId, tasks.get(0).getTodolistId());
    }
}