package com.danyatheworst.task_manager.task;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;

    @Column
    private Boolean isDone;

    @Column(name = "status_updated_at")
    private LocalDateTime statusUpdatedAt;

    @JoinColumn(name = "todolist_id", nullable = false)
    private Long todolistId;

    public Task(String title, Long todolistId) {
        this.title = title;
        this.todolistId = todolistId;
        this.isDone = false;
    }
}

