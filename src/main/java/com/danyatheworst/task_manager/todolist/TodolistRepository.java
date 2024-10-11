package com.danyatheworst.task_manager.todolist;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TodolistRepository extends JpaRepository<Todolist, Long> { }