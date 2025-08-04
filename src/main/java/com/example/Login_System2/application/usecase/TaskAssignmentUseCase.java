package com.example.Login_System2.application.usecase;

import com.example.Login_System2.domain.model.TaskAssignment;
import com.example.Login_System2.domain.model.AssignmentStatus;
import com.example.Login_System2.domain.model.Task;
import com.example.Login_System2.domain.model.User;
import com.example.Login_System2.domain.port.TaskAssignmentRepository;
import com.example.Login_System2.domain.port.TaskRepository;
import com.example.Login_System2.domain.port.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@AllArgsConstructor
public class TaskAssignmentUseCase {
    private final TaskAssignmentRepository taskAssignmentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    private final Logger log = LoggerFactory.getLogger(TaskAssignmentUseCase.class);

    // Görev atama
    public Optional<TaskAssignment> assignTask(int requesterId, String requesterRole, int taskId, int assignedToId, String message) {
        log.info("Görev atama isteği: requesterId={}, requesterRole={}, taskId={}, assignedToId={}", 
                 requesterId, requesterRole, taskId, assignedToId);

        // Görev var mı kontrol et
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            log.warn("Görev bulunamadı! taskId={}", taskId);
            return Optional.empty();
        }

        // Atanacak kullanıcı var mı kontrol et
        Optional<User> assignedToOpt = userRepository.findById(assignedToId);
        if (assignedToOpt.isEmpty()) {
            log.warn("Atanacak kullanıcı bulunamadı! assignedToId={}", assignedToId);
            return Optional.empty();
        }

        // Yetki kontrolü
        if (requesterRole.equals("USER")) {
            log.warn("USER rolü görev atayamaz! requesterId={}", requesterId);
            return Optional.empty();
        }

        List<TaskAssignment> existingAssignments = taskAssignmentRepository.findByTaskId(taskId);
        boolean alreadyAssigned = existingAssignments.stream()
            .anyMatch(a -> a.getAssignedTo().getId() == assignedToId && 
                        (a.getStatus() == AssignmentStatus.PENDING || a.getStatus() == AssignmentStatus.ACCEPTED));

        if (alreadyAssigned) {
            log.warn("Bu kullanıcı zaten bu göreve atanmış! assignedToId={}", assignedToId);
            return Optional.empty();
        }

        Task task = taskOpt.get();
        User assignedTo = assignedToOpt.get();

        // Yeni atama oluştur
        TaskAssignment assignment = new TaskAssignment();
        assignment.setTask(task);
        assignment.setAssignedTo(assignedTo);
        assignment.setStatus(AssignmentStatus.PENDING);
        assignment.setAssignedAt(LocalDateTime.now());
        assignment.setMessage(message);

        // Atayan kişiyi set et
        Optional<User> requesterOpt = userRepository.findById(requesterId);
        if (requesterOpt.isPresent()) {
            assignment.setAssignedBy(requesterOpt.get());
        }

        TaskAssignment savedAssignment = taskAssignmentRepository.save(assignment);
        log.info("Görev başarıyla atandı! assignmentId={}", savedAssignment.getId());
        
        return Optional.of(savedAssignment);
    }

    // Kullanıcının kendi atamalarını getir
    public List<TaskAssignment> getMyAssignments(int userId) {
        log.info("Kullanıcının atamalarını getirme: userId={}", userId);
        return taskAssignmentRepository.findByAssignedToId(userId);
    }

    // Atamaya yanıt verme (kabul/red)
    public Optional<TaskAssignment> respondToAssignment(int userId, int assignmentId, AssignmentStatus response, String message) {
        log.info("Atama yanıtı: userId={}, assignmentId={}, response={}", userId, assignmentId, response);

        Optional<TaskAssignment> assignmentOpt = taskAssignmentRepository.findById(assignmentId);
        if (assignmentOpt.isEmpty()) {
            log.warn("Atama bulunamadı! assignmentId={}", assignmentId);
            return Optional.empty();
        }

        TaskAssignment assignment = assignmentOpt.get();

        // Sadece atanan kişi yanıt verebilir
        if (assignment.getAssignedTo().getId() != userId) {
            log.warn("Sadece atanan kişi yanıt verebilir! userId={}, assignedToId={}", 
                     userId, assignment.getAssignedTo().getId());
            return Optional.empty();
        }

        // Sadece PENDING durumundaki atamalar yanıtlanabilir
        if (assignment.getStatus() != AssignmentStatus.PENDING) {
            log.warn("Sadece PENDING durumundaki atamalar yanıtlanabilir! status={}", assignment.getStatus());
            return Optional.empty();
        }

        assignment.setStatus(response);
        assignment.setRespondedAt(LocalDateTime.now());

        // Eğer kabul edildiyse, task'in currentAssignee'sini güncelle
        if (response == AssignmentStatus.ACCEPTED) {
            Task task = assignment.getTask();
            task.setCurrentAssignee(assignment.getAssignedTo());
            taskRepository.save(task);
            log.info("Task currentAssignee güncellendi: taskId={}, newAssigneeId={}", 
                    task.getId(), assignment.getAssignedTo().getId());
        }

        TaskAssignment savedAssignment = taskAssignmentRepository.save(assignment);
        log.info("Atama yanıtı kaydedildi! assignmentId={}, response={}", assignmentId, response);
        
        return Optional.of(savedAssignment);
    }

    // Tüm atamaları getir (ADMIN/MANAGER için)
    public List<TaskAssignment> getAllAssignments(int requesterId, String requesterRole) {
        log.info("Tüm atamaları getirme: requesterId={}, requesterRole={}", requesterId, requesterRole);

        if (requesterRole.equals("USER")) {
            log.warn("USER rolü tüm atamaları göremez!");
            return List.of();
        }

        return taskAssignmentRepository.findAll();
    }

    // Belirli bir görevin atamalarını getir
    public List<TaskAssignment> getTaskAssignments(int taskId, int requesterId, String requesterRole) {
        log.info("Görev atamalarını getirme: taskId={}, requesterId={}, requesterRole={}", 
                 taskId, requesterId, requesterRole);

        // USER sadece kendi görevlerinin atamalarını görebilir
        if (requesterRole.equals("USER")) {
            Optional<Task> taskOpt = taskRepository.findById(taskId);
            if (taskOpt.isPresent() && taskOpt.get().getOwner().getId() != requesterId) {
                log.warn("USER sadece kendi görevlerinin atamalarını görebilir!");
                return List.of();
            }
        }

        return taskAssignmentRepository.findByTaskId(taskId);
    }
}
