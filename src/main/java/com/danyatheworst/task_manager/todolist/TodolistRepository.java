package com.danyatheworst.task_manager.todolist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TodolistRepository extends JpaRepository<Todolist, Long> {

    @Query("SELECT t FROM Todolist t WHERE t.userId = :userId ORDER BY t.id")
    List<Todolist> findAllByUserId(Long userId);
}