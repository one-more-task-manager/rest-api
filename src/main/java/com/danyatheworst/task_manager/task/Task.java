package com.danyatheworst.task_manager.task;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @JoinColumn(name = "todolist_id", nullable = false)
    private Long todolistId;

    public Task(String title, Long todolistId) {
        this.title = title;
        this.todolistId = todolistId;
        this.isDone = false;
    }
}


