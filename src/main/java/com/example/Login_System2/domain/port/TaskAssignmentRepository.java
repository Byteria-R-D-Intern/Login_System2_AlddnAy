package com.example.Login_System2.domain.port;

import java.util.List;
import java.util.Optional;
import com.example.Login_System2.domain.model.TaskAssignment;
import com.example.Login_System2.domain.model.AssignmentStatus;

public interface TaskAssignmentRepository {

    TaskAssignment save(TaskAssignment taskAssignment);
    Optional<TaskAssignment> findById(int id);
    List<TaskAssignment> findByTaskId(int taskId);
    List<TaskAssignment> findByAssignedToId(int assignedToId);
    List<TaskAssignment> findByAssignedById(int assignedById);
    List<TaskAssignment> findByStatus(AssignmentStatus status);
    void delete(TaskAssignment taskAssignment);
    List<TaskAssignment> findAll();
    
}
