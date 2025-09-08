package com.example.Login_System2.infrastructure.repository;

import com.example.Login_System2.domain.model.Notification;
import com.example.Login_System2.domain.model.NotificationType;
import com.example.Login_System2.domain.port.NotificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaNotification extends JpaRepository<Notification, Integer>, NotificationRepository {
    Optional<Notification> findById(int id);
    List<Notification> findByUserId(int userId);
    List<Notification> findByUserIdAndReadFlagFalse(int userId);
    List<Notification> findByType(NotificationType type);
    List<Notification> findAll();
}


