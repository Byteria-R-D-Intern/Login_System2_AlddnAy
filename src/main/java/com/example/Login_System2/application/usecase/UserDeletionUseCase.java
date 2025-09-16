package com.example.Login_System2.application.usecase;

import com.example.Login_System2.domain.model.Task;
import com.example.Login_System2.domain.model.User;
import com.example.Login_System2.domain.port.TaskRepository;
import com.example.Login_System2.domain.port.UserRepository;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserDeletionUseCase {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final Logger log = LoggerFactory.getLogger(UserDeletionUseCase.class);

    @Transactional
    public boolean deleteUser(int requesterId, String requesterRole, int userId) {
        log.info("Kullanıcı silme isteği: requesterId={}, requesterRole={}, userId={}", requesterId, requesterRole, userId);

        if (!"ADMIN".equals(requesterRole)) {
            log.warn("Sadece ADMIN kullanıcı silebilir! requesterRole={}", requesterRole);
            return false;
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            log.warn("Kullanıcı bulunamadı! userId={}", userId);
            return false;
        }
        User user = userOpt.get();

        // 1) Kullanıcının owner olduğu görevleri sahipsiz hale getir (NULL)
        List<Task> ownedTasks = taskRepository.findByOwnerId(userId);
        for (Task task : ownedTasks) {
            task.setOwner(null);
            taskRepository.save(task);
            log.info("Görev sahipliği boşaltıldı: taskId={} (owner=null)", task.getId());
        }

        // 2) Kullanıcının currentAssignee olduğu görevleri boşalt
        List<Task> assignedNow = taskRepository.findByCurrentAssigneeId(userId);
        for (Task task : assignedNow) {
            task.setCurrentAssignee(null);
            taskRepository.save(task);
            log.info("Görev ataması boşaltıldı: taskId={} (currentAssignee=null)", task.getId());
        }

        // 3) Diğer ilişkileri veritabanındaki FK kuralları temizler (CASCADE / SET NULL)
        userRepository.delete(user);
        log.info("Kullanıcı silindi: userId={}", userId);
        return true;
    }
}


