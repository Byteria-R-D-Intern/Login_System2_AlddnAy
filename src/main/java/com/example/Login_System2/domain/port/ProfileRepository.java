package com.example.Login_System2.domain.port;

import com.example.Login_System2.domain.model.UserProfile;
import java.util.Optional;

public interface ProfileRepository {
    Optional<UserProfile> findByUserId(int userId);
    UserProfile save(UserProfile userProfile); // Ekle!
    void delete(UserProfile userProfile);   
}
