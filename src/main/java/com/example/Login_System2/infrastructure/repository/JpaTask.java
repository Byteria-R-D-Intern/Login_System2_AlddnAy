package com.example.Login_System2.infrastructure.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Login_System2.domain.model.Priority;
import com.example.Login_System2.domain.model.Task;
import com.example.Login_System2.domain.port.TaskRepository;

import com.example.Login_System2.domain.model.Status;



public  interface JpaTask extends JpaRepository<Task, Integer>, TaskRepository {

    List<Task> findByOwnerId(int ownerId);
    List<Task> findByStatus(Status status);
    List<Task> findByPriority(Priority priority);
    List<Task> findByTitleContaining(String title);

}
