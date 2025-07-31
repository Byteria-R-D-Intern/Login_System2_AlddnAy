package com.example.Login_System2.application.usecase;

import com.example.Login_System2.domain.model.Priority;
import com.example.Login_System2.domain.model.Status;
import com.example.Login_System2.domain.model.Task;
import com.example.Login_System2.domain.port.TaskRepository;
import com.example.Login_System2.domain.port.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@AllArgsConstructor
public class TaskUseCase {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    private final Logger log = LoggerFactory.getLogger(TaskUseCase.class);

    public Optional<Task> createTask(int requestId, String requestRole, Task newTask){
        log.info("Görev oluşturma isteği: requesterId={}, requesterRole={}", requestId, requestRole);

        if(requestRole.equals("USER") && requestId != newTask.getOwner().getId()){
            log.warn("USER sadece kendi görevlerini oluşturabilir! requesterId={}, ownerId={}", requestId, newTask.getOwner().getId());
            return Optional.empty();
        }

        if (userRepository.findById(newTask.getOwner().getId()).isEmpty()) {
            log.warn("Kullanıcı bulunamadı! ownerId={}", newTask.getOwner().getId());
            return Optional.empty();
        }

        log.info("Görev oluşturuluyor... ownerId={}", newTask.getOwner().getId());
        Task savedTask = taskRepository.save(newTask);
        log.info("Görev başarıyla oluşturuldu! taskId={}", savedTask.getId());
        return Optional.of(savedTask);
    }

    public Optional<Task> getTask(int requestId, String requestRole, int taskId){
        log.info("Görev görüntüleme isteği: requesterId={}, requesterRole={}, taskId={}", requestId, requestRole, taskId);
        Optional<Task> taskOpt = taskRepository.findById(taskId);

        if(taskOpt.isEmpty()){
            log.warn("Görev bulunamadı! taskId={}", taskId);
            return Optional.empty();
        }

        Task task = taskOpt.get();
        
        if(requestRole.equals("USER") && task.getOwner().getId() != requestId){
            log.warn("USER sadece kendi görevlerini görebilir! requesterId={}, ownerId={}", requestId, task.getOwner().getId());
            return Optional.empty();
        }

        log.info("Görev başarıyla getirildi! taskId={}", taskId);
        return Optional.of(task);
    }

    public Optional<Task> updateTask(int requestId, String requestRole, int taskId, Task updatedTask) {
        log.info("Görev güncelleme isteği: requesterId={}, requesterRole={}, taskId={}", requestId, requestRole, taskId);
        
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            log.warn("Görev bulunamadı! taskId={}", taskId);
            return Optional.empty();
        }
        
        Task existingTask = taskOpt.get();
        
        // USER sadece kendi görevlerini güncelleyebilir
        if (requestRole.equals("USER") && requestId != existingTask.getOwner().getId()) {
            log.warn("USER sadece kendi görevlerini güncelleyebilir! requesterId={}, ownerId={}", requestId, existingTask.getOwner().getId());
            return Optional.empty();
        }
        
        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setStatus(updatedTask.getStatus());
        existingTask.setPriority(updatedTask.getPriority());

        Task savedTask = taskRepository.save(existingTask);
        log.info("Görev başarıyla güncellendi! taskId={}", taskId);
        return Optional.of(savedTask);
    }

    public boolean deleteTask(int requesterId, String requesterRole, int taskId) {
        log.info("Görev silme isteği: requesterId={}, requesterRole={}, taskId={}", requesterId, requesterRole, taskId);
        
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            log.warn("Görev bulunamadı! taskId={}", taskId);
            return false;
        }
        
        Task task = taskOpt.get();
        
        // USER sadece kendi görevlerini silebilir
        if ("USER".equals(requesterRole) && requesterId != task.getOwner().getId()) {
            log.warn("USER sadece kendi görevlerini silebilir! requesterId={}, ownerId={}", requesterId, task.getOwner().getId());
            return false;
        }
        
        taskRepository.delete(task);
        log.info("Görev başarıyla silindi! taskId={}", taskId);
        return true;
    }

    public List<Task> getAllTasks(int requestId, String requestRole, Integer ownerId, Status status, Priority priority, String title) {
        log.info("Tüm görevleri getirme isteği: requesterId={}, requesterRole={}, ownerId={}, status={}, priority={}, title={}", 
                 requestId, requestRole, ownerId, status, priority, title);

        // USER sadece kendi görevlerini görebilir
        if (requestRole.equals("USER")) {
            List<Task> userTasks = taskRepository.findByOwnerId(requestId);
            
            return userTasks.stream()
                .filter(task -> status == null || task.getStatus() == status)
                .filter(task -> priority == null || task.getPriority() == priority)
                .filter(task -> title == null || title.isEmpty() || 
                              task.getTitle().toLowerCase().contains(title.toLowerCase()))
                .collect(Collectors.toList());
        }

        // MANAGER ve ADMIN için tüm görevler + filtreleme
        List<Task> allTasks = taskRepository.findAll();
        
        return allTasks.stream()
            .filter(task -> ownerId == null || task.getOwner().getId() == ownerId)
            .filter(task -> status == null || task.getStatus() == status)
            .filter(task -> priority == null || task.getPriority() == priority)
            .filter(task -> title == null || title.isEmpty() || 
                          task.getTitle().toLowerCase().contains(title.toLowerCase()))
            .collect(Collectors.toList());
    }
}
