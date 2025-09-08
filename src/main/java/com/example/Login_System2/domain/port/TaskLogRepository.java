package com.example.Login_System2.domain.port;

import com.example.Login_System2.domain.model.TaskLog;
import com.example.Login_System2.domain.model.TaskLogAction;

import java.util.List;
import java.util.Optional;

public interface TaskLogRepository {
    TaskLog save(TaskLog log);
    Optional<TaskLog> findById(int id);
    List<TaskLog> findByTaskId(int taskId);
    List<TaskLog> findByActorId(int actorId);
    List<TaskLog> findByTargetId(int targetId);
    List<TaskLog> findByCommentId(int commentId);
    List<TaskLog> findByAction(TaskLogAction action);
    List<TaskLog> findAll();
}


