package com.danyatheworst.task_manager.task;

import com.danyatheworst.task_manager.auth.dto.JwtUserDetailsDto;
import com.danyatheworst.task_manager.auth.jwt.JwtService;
import com.danyatheworst.task_manager.task.dto.RequestCreateTaskDto;
import com.danyatheworst.task_manager.task.dto.RequestUpdateTaskDto;
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
import java.util.Optional;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
public class TaskIT {
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
    private Todolist todolist;

    @BeforeEach
    void setUp() {
        User user = this.userRepository.save(new User("username@gmail.com", "password"));
        this.todolist = this.todolistRepository.save(new Todolist("todolist", user.getId()));
        JwtUserDetailsDto jwtUserDetailsDto = new JwtUserDetailsDto(
                user.getId(), user.getUsername(), user.getAuthorities()
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
    void itShouldReturn201StatusCodeWhenTaskWasCreated() throws Exception {
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

    @Test
    void itShouldReturn201StatusCodeWhenTaskWasRemoved() throws Exception {
        Long todolistId = this.todolist.getId();
        Task task = this.taskRepository.save(new Task("task-test", todolistId));

        this.mockMvc.perform(MockMvcRequestBuilders.delete(
                                "/todolists/{todolistId}/tasks/{taskId}", todolistId, task.getId()
                        )
                        .header("Authorization", "Bearer " + this.accessToken))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        List<Task> tasks = this.taskRepository.findAll();
        Assertions.assertEquals(0, tasks.size());
    }

    @Test
    void itShouldReturn204StatusCodeWhenUpdateTask() throws Exception {
        Long todolistId = this.todolist.getId();
        Task task = this.taskRepository.save(new Task("task-test", todolistId));
        String updatedTitle = "update-title-task-test";
        boolean updatedStatus = true;
        RequestUpdateTaskDto payload = new RequestUpdateTaskDto(updatedTitle, updatedStatus);

        this.mockMvc.perform(MockMvcRequestBuilders.patch(
                                "/todolists/{todolistId}/tasks/{taskId}", todolistId, task.getId()
                        )
                        .header("Authorization", "Bearer " + this.accessToken)
                        .content(this.objectMapper.writeValueAsString(payload))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        Optional<Task> updatedTask = this.taskRepository.findById(todolist.getId());
        Assertions.assertTrue(updatedTask.isPresent());
        Assertions.assertEquals(task.getId(), updatedTask.get().getId());
        Assertions.assertEquals(updatedTitle, updatedTask.get().getTitle());
        Assertions.assertEquals(updatedStatus, updatedTask.get().getIsDone());
    }
}