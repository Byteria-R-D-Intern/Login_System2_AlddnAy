package com.example.Login_System2.application.usecase;

import com.example.Login_System2.domain.model.Notification;
import com.example.Login_System2.domain.model.NotificationType;
import com.example.Login_System2.domain.model.TaskLog;
import com.example.Login_System2.domain.model.User;
import com.example.Login_System2.domain.port.NotificationRepository;
import com.example.Login_System2.domain.port.UserRepository;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class NotificationUseCase {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Logger log = LoggerFactory.getLogger(NotificationUseCase.class);

    public Optional<Notification> createNotification(int userId, NotificationType type, String title, String message) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            log.warn("Bildirim için kullanıcı bulunamadı! userId={}", userId);
            return Optional.empty();
        }

        Notification n = new Notification();
        n.setUser(userOpt.get());
        n.setType(type);
        n.setTitle(title);
        n.setMessage(message);

        Notification saved = notificationRepository.save(n);
        return Optional.of(saved);
    }

    public List<Notification> getMyNotifications(int userId) {
        return notificationRepository.findByUserId(userId);
    }

    public List<Notification> getUnreadNotifications(int userId) {
        return notificationRepository.findByUserIdAndReadFlagFalse(userId);
    }

    public Optional<Notification> markAsRead(int notificationId, int requesterId) {
        Optional<Notification> opt = notificationRepository.findById(notificationId);
        if (opt.isEmpty()) return Optional.empty();
        Notification n = opt.get();
        if (n.getUser().getId() != requesterId) return Optional.empty();
        n.setReadFlag(true);
        n.setReadAt(java.time.LocalDateTime.now());
        return Optional.of(notificationRepository.save(n));
    }

    // TaskLog'dan bildirim oluşturma (basit eşleme)
    public Optional<Notification> createFromTaskLog(TaskLog taskLog, int recipientUserId) {
        Optional<User> userOpt = userRepository.findById(recipientUserId);
        if (userOpt.isEmpty()) {
            log.warn("recipient not found for notification from taskLog id={}", taskLog.getId());
            return Optional.empty();
        }

        NotificationType type = mapActionToType(taskLog);
        String title = buildTitle(taskLog);
        String message = taskLog.getMessage();

        Notification n = new Notification();
        n.setUser(userOpt.get());
        n.setType(type);
        n.setTitle(title);
        n.setMessage(message);
        n.setTaskLog(taskLog);

        ObjectNode data = objectMapper.createObjectNode();
        data.put("taskId", taskLog.getTask().getId());
        data.put("taskLogId", taskLog.getId());
        if (taskLog.getComment() != null) {
            data.put("commentId", taskLog.getComment().getId());
        }
        n.setData(data);

        Notification saved = notificationRepository.save(n);
        return Optional.of(saved);
    }

    private NotificationType mapActionToType(TaskLog log) {
        switch (log.getAction()) {
            case ASSIGNED: return NotificationType.TASK_ASSIGNED;
            case ASSIGNMENT_RESPONDED: return NotificationType.ASSIGNMENT_RESPONDED;
            case COMMENT_ADDED: return NotificationType.COMMENT_ADDED;
            case COMMENT_DELETED: return NotificationType.COMMENT_DELETED;
            default: return NotificationType.TASK_UPDATED;
        }
    }

    private String buildTitle(TaskLog log) {
        switch (log.getAction()) {
            case ASSIGNED:
                return "Görev atandı";
            case ASSIGNMENT_RESPONDED:
                return "Atamaya yanıt geldi";
            case COMMENT_ADDED:
                return "Yeni yorum";
            case COMMENT_DELETED:
                return "Yorum silindi";
            case STATUS_CHANGED:
                return "Durum güncellendi";
            case PRIORITY_CHANGED:
                return "Öncelik güncellendi";
            default:
                return "Görev güncellendi";
        }
    }
}


