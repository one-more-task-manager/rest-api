package com.danyatheworst.task_manager.todolist;

import com.danyatheworst.task_manager.auth.dto.JwtUserDetailsDto;
import com.danyatheworst.task_manager.auth.jwt.JwtService;
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

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String accessToken;
    private User user;

    @BeforeEach
    void setUp() {
        this.user = this.userRepository.save(new User("username@gmail.com", "password"));
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

        this.mockMvc.perform(MockMvcRequestBuilders.post("/todolists")
                        .header("Authorization", "Bearer " + this.accessToken)
                        .content(this.objectMapper.writeValueAsString(payload))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        List<Todolist> todolists = this.todolistRepository.findAll();
        Assertions.assertEquals(1, todolists.size());
        Assertions.assertEquals(title, todolists.get(0).getTitle());
        Assertions.assertEquals(user.getId(), todolists.get(0).getUserId());
    }

    @Test
    void itShouldReturn201StatusCodeWhenTodolistWasRemoved() throws Exception {
        Todolist todolist = this.todolistRepository.save(new Todolist("todolist-test", this.user.getId()));

        this.mockMvc.perform(MockMvcRequestBuilders.delete("/todolists/{id}", todolist.getId())
                        .header("Authorization", "Bearer " + this.accessToken))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        List<Todolist> todolists = this.todolistRepository.findAll();
        Assertions.assertEquals(0, todolists.size());
    }

    @Test
    void itShouldReturn404StatusCodeWhenTodolistToDeleteDoesNotExist() throws Exception {
        Long nonExistentId = 1234567854321L;
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/todolists/{id}", nonExistentId)
                        .header("Authorization", "Bearer " + this.accessToken))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void itShouldReturn403StatusCodeWhenTodolistToDeleteDoesNotBelongToUser() throws Exception {
        String todolistTitle = "todolist-test";
        Todolist todolist = this.todolistRepository.save(new Todolist(todolistTitle, this.user.getId()));
        User maliciousUser = this.userRepository.save(new User("malicious-user@gmail.com", "malicious password"));
        JwtUserDetailsDto jwtUserDetailsDto = new JwtUserDetailsDto(
                maliciousUser.getId(), maliciousUser.getUsername(), maliciousUser.getAuthorities()
        );
        String maliciousToken = this.jwtService.generateAccessToken(jwtUserDetailsDto);

        this.mockMvc.perform(MockMvcRequestBuilders.delete("/todolists/{id}", todolist.getId())
                        .header("Authorization", "Bearer " + maliciousToken))
                .andExpect(MockMvcResultMatchers.status().isForbidden());

        List<Todolist> todolists = this.todolistRepository.findAll();
        Assertions.assertEquals(1, todolists.size());
        Assertions.assertEquals(todolistTitle, todolists.get(0).getTitle());
    }

    @Test
    void itShouldReturn404StatusCodeWhenFetchingTodolistThatDoesNotExist() throws Exception {
        String title = "todolist-test";
        Todolist todolist = this.todolistRepository.save(new Todolist(title, this.user.getId()));
        User maliciousUser = this.userRepository.save(new User("malicious-user@gmail.com", "malicious password"));
        JwtUserDetailsDto jwtUserDetailsDto = new JwtUserDetailsDto(
                maliciousUser.getId(), maliciousUser.getUsername(), maliciousUser.getAuthorities()
        );
        String maliciousToken = this.jwtService.generateAccessToken(jwtUserDetailsDto);

        this.mockMvc.perform(MockMvcRequestBuilders.delete("/todolists/{id}", todolist.getId())
                        .header("Authorization", "Bearer " + maliciousToken))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void itShouldReturn204StatusCodeWhenUpdateTodolist() throws Exception {
        String title = "todolist";
        Todolist todolist = this.todolistRepository.save(new Todolist(title, this.user.getId()));
        RequestUpdateTodolistDto payload = new RequestUpdateTodolistDto("updated-todolist");

        this.mockMvc.perform(MockMvcRequestBuilders.patch("/todolists/{id}", todolist.getId())
                        .header("Authorization", "Bearer " + this.accessToken)
                        .content(this.objectMapper.writeValueAsString(payload))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        Optional<Todolist> updatedTodolist = this.todolistRepository.findById(todolist.getId());
        Assertions.assertTrue(updatedTodolist.isPresent());
        Assertions.assertEquals(todolist.getId(), updatedTodolist.get().getId());
        Assertions.assertEquals(payload.getTitle(), updatedTodolist.get().getTitle());
    }

    //403 when updating
    //404 when updating
}
