package com.example.Login_System2.infrastructure.repository;

import com.example.Login_System2.domain.model.TaskComment;
import com.example.Login_System2.domain.port.TaskCommentRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaComment extends JpaRepository<TaskComment, Integer>, TaskCommentRepository {
    
    @Override
    default List<TaskComment> findByTaskId(int taskId) {
        return findAll().stream()
            .filter(comment -> comment.getTask().getId() == taskId)
            .toList();
    }
    
    @Override
    default List<TaskComment> findByUserId(int userId) {
        return findAll().stream()
            .filter(comment -> comment.getUser().getId() == userId)
            .toList();
    }
    
    @Override
    default boolean existsById(int id) {
        return findById(id).isPresent();
    }
}
