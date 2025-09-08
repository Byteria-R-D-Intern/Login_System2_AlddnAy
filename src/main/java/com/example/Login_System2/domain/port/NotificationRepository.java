package com.example.Login_System2.domain.port;

import com.example.Login_System2.domain.model.Notification;
import com.example.Login_System2.domain.model.NotificationType;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository {
    Notification save(Notification notification);
    Optional<Notification> findById(int id);
    List<Notification> findByUserId(int userId);
    List<Notification> findByUserIdAndReadFlagFalse(int userId);
    List<Notification> findByType(NotificationType type);
    List<Notification> findAll();
    void delete(Notification notification);
}


