package com.danyatheworst.task_manager.todolist;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TodolistRepository extends JpaRepository<Todolist, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM Todolist t WHERE t.id = :id AND t.userId = :userId")
    int deleteById(Long id, Long userId);
}