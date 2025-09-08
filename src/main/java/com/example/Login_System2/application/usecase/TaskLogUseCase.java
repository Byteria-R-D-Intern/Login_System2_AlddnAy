package com.example.Login_System2.application.usecase;

import com.example.Login_System2.domain.model.Task;
import com.example.Login_System2.domain.model.TaskLog;
import com.example.Login_System2.domain.model.TaskLogAction;
import com.example.Login_System2.domain.model.User;
import com.example.Login_System2.domain.port.TaskLogRepository;
import com.example.Login_System2.domain.port.TaskRepository;
import com.example.Login_System2.domain.port.UserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Comparator;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class TaskLogUseCase {

    private final TaskLogRepository taskLogRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final NotificationUseCase notificationUseCase;

    private final Logger log = LoggerFactory.getLogger(TaskLogUseCase.class);

    @Transactional
    public Optional<TaskLog> logAction(int taskId, int actorId, TaskLogAction action, String details) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        Optional<User> actorOpt = userRepository.findById(actorId);
        if (taskOpt.isEmpty() || actorOpt.isEmpty()) {
            log.warn("Log için task/actor bulunamadı! taskId={}, actorId={}", taskId, actorId);
            return Optional.empty();
        }

        TaskLog logEntry = new TaskLog();
        logEntry.setTask(taskOpt.get());
        logEntry.setActor(actorOpt.get());
        logEntry.setAction(action);
        logEntry.setMessage(details);

        TaskLog saved = taskLogRepository.save(logEntry);

        // Basit kural: hedef varsa ona, yoksa task owner'a bildirim üret
        int recipientId = (logEntry.getTarget() != null)
                ? logEntry.getTarget().getId()
                : logEntry.getTask().getOwner().getId();
        notificationUseCase.createFromTaskLog(saved, recipientId);

        return Optional.of(saved);
    }

    // Target kullanıcıyı log'a dahil ederek loglama (örn: ASSIGNED)
    @Transactional
    public Optional<TaskLog> logActionWithTarget(int taskId, int actorId, Integer targetUserId,
                                                 TaskLogAction action, String details) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        Optional<User> actorOpt = userRepository.findById(actorId);
        if (taskOpt.isEmpty() || actorOpt.isEmpty()) {
            log.warn("Log için task/actor bulunamadı! taskId={}, actorId={}", taskId, actorId);
            return Optional.empty();
        }

        User targetUser = null;
        if (targetUserId != null) {
            targetUser = userRepository.findById(targetUserId).orElse(null);
        }

        TaskLog logEntry = new TaskLog();
        logEntry.setTask(taskOpt.get());
        logEntry.setActor(actorOpt.get());
        logEntry.setTarget(targetUser);
        logEntry.setAction(action);
        logEntry.setMessage(details);

        TaskLog saved = taskLogRepository.save(logEntry);

        int recipientId = (targetUser != null) ? targetUser.getId() : logEntry.getTask().getOwner().getId();
        notificationUseCase.createFromTaskLog(saved, recipientId);

        return Optional.of(saved);
    }

    public List<TaskLog> getTaskLogs(int taskId) {
        return taskLogRepository.findByTaskId(taskId);
    }

    public List<TaskLog> getActorLogs(int actorId) {
        return taskLogRepository.findByActorId(actorId);
    }

    public List<TaskLog> getAllLogs() {
        List<TaskLog> all = taskLogRepository.findAll();
        all.sort(Comparator.comparing(TaskLog::getCreatedAt).reversed());
        return all;
    }
}


