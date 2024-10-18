package com.danyatheworst.task_manager.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query("SELECT t FROM Task t WHERE t.todolistId = :todolistId ORDER BY t.id")
    List<Task> findAllByTodolistId(Long todolistId);
}
