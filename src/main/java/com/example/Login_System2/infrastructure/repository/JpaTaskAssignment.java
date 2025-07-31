package com.example.Login_System2.infrastructure.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Login_System2.domain.model.TaskAssignment;
import com.example.Login_System2.domain.model.AssignmentStatus;
import com.example.Login_System2.domain.port.TaskAssignmentRepository;

public interface JpaTaskAssignment extends JpaRepository<TaskAssignment, Integer>, TaskAssignmentRepository {
    
    List<TaskAssignment> findByTaskId(int taskId);
    List<TaskAssignment> findByAssignedToId(int userId);
    List<TaskAssignment> findByAssignedById(int userId);
    List<TaskAssignment> findByStatus(AssignmentStatus status);
    List<TaskAssignment> findByTaskIdAndStatus(int taskId, AssignmentStatus status);
}