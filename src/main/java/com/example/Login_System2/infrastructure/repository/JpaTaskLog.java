package com.example.Login_System2.infrastructure.repository;

import com.example.Login_System2.domain.model.TaskLog;
import com.example.Login_System2.domain.model.TaskLogAction;
import com.example.Login_System2.domain.port.TaskLogRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaTaskLog extends JpaRepository<TaskLog, Integer>, TaskLogRepository {
    Optional<TaskLog> findById(int id);
    List<TaskLog> findByTaskId(int taskId);
    List<TaskLog> findByActorId(int actorId);
    List<TaskLog> findByTargetId(int targetId);
    List<TaskLog> findByCommentId(int commentId);
    List<TaskLog> findByAction(TaskLogAction action);
    
}


