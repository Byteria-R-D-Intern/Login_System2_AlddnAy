package com.example.Login_System2.domain.port;

import java.util.List;
import java.util.Optional;

import com.example.Login_System2.domain.model.Priority;
import com.example.Login_System2.domain.model.Task;

import ch.qos.logback.core.status.Status;



public interface TaskRepository {
    Task save(Task task);
    Optional<Task> findById(int id);
    List<Task> findByOwnerId(int ownerId);
    List<Task> findByStatus(Status status);
    List<Task> findByPriority(Priority priority);
    List<Task> findByTitleContaining(String title);
    void delete(Task task);
}


