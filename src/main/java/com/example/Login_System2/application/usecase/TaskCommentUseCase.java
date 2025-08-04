package com.example.Login_System2.application.usecase;
import com.example.Login_System2.domain.model.Task;
import com.example.Login_System2.domain.model.TaskComment;
import com.example.Login_System2.domain.model.User;
import com.example.Login_System2.domain.port.TaskCommentRepository;
import com.example.Login_System2.domain.port.TaskRepository;
import com.example.Login_System2.domain.port.UserRepository;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@AllArgsConstructor
public class TaskCommentUseCase {
    private final TaskCommentRepository taskCommentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    private final Logger log = LoggerFactory.getLogger(TaskCommentUseCase.class);

    // Yorum ekleme
    public Optional<TaskComment> addComment(int requesterId, String requesterRole, int taskId, String comment) {
        log.info("Yorum ekleme isteği: requesterId={}, requesterRole={}, taskId={}", requesterId, requesterRole, taskId);

        // Task var mı kontrol et
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            log.warn("Görev bulunamadı! taskId={}", taskId);
            return Optional.empty();
        }

        Task task = taskOpt.get();
        log.info("Task bulundu! Task owner ID: {}", task.getOwner().getId());

        // Kullanıcı var mı kontrol et
        Optional<User> userOpt = userRepository.findById(requesterId);
        if (userOpt.isEmpty()) {
            log.warn("Kullanıcı bulunamadı! requesterId={}", requesterId);
            return Optional.empty();
        }

        User user = userOpt.get();

        // Yetki kontrolü
        if (requesterRole.equals("USER") && task.getOwner().getId() != requesterId) {
            log.warn("USER sadece kendi görevlerine yorum yapabilir! requesterId={}, ownerId={}", requesterId, task.getOwner().getId());
            return Optional.empty();
        }

        // Yorum oluştur
        TaskComment newComment = new TaskComment();
        newComment.setTask(task);
        newComment.setUser(user);
        newComment.setComment(comment);

        TaskComment savedComment = taskCommentRepository.save(newComment);
        log.info("Yorum başarıyla eklendi! commentId={}", savedComment.getId());
        
        return Optional.of(savedComment);
    }

    // Görevin yorumlarını getir
    public List<TaskComment> getTaskComments(int taskId, int requesterId, String requesterRole) {
        log.info("Görev yorumlarını getirme: taskId={}, requesterId={}, requesterRole={}", taskId, requesterId, requesterRole);

        // Task var mı kontrol et
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            log.warn("Görev bulunamadı! taskId={}", taskId);
            return List.of();
        }

        Task task = taskOpt.get();

        // USER sadece kendi görevlerinin yorumlarını görebilir
        if (requesterRole.equals("USER") && task.getOwner().getId() != requesterId) {
            log.warn("USER sadece kendi görevlerinin yorumlarını görebilir!");
            return List.of();
        }

        return taskCommentRepository.findByTaskId(taskId);
    }

    // Yorum silme
    public boolean deleteComment(int requesterId, String requesterRole, int commentId) {
        log.info("Yorum silme isteği: requesterId={}, requesterRole={}, commentId={}", requesterId, requesterRole, commentId);

        Optional<TaskComment> commentOpt = taskCommentRepository.findById(commentId);
        if (commentOpt.isEmpty()) {
            log.warn("Yorum bulunamadı! commentId={}", commentId);
            return false;
        }

        TaskComment comment = commentOpt.get();

        // Yetki kontrolü
        if (requesterRole.equals("USER") && comment.getUser().getId() != requesterId) {
            log.warn("USER sadece kendi yorumlarını silebilir! requesterId={}, commentUserId={}", requesterId, comment.getUser().getId());
            return false;
        }

        taskCommentRepository.delete(comment);
        log.info("Yorum başarıyla silindi! commentId={}", commentId);
        return true;
    }
}