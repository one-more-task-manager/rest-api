package com.danyatheworst.task_manager.todolist;

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
@Table(name = "todolists")
public class Todolist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;

    @JoinColumn(name = "user_id", nullable = false)
    private Long userId;

    public Todolist(String title, Long userId) {
        this.title = title;
        this.userId = userId;
    }
}
