package com.example.Login_System2.domain.port;

import com.example.Login_System2.domain.model.TaskComment;
import java.util.List;
import java.util.Optional;

public interface TaskCommentRepository {
    TaskComment save(TaskComment comment);
    Optional<TaskComment> findById(int id);
    List<TaskComment> findAll();
    List<TaskComment> findByTaskId(int taskId);
    List<TaskComment> findByUserId(int userId);
    void delete(TaskComment comment);
    boolean existsById(int id);
}
