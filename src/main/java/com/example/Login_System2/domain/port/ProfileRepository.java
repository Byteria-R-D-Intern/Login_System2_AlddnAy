package com.example.Login_System2.domain.port;

import com.example.Login_System2.domain.model.UserProfile;

import java.util.List;
import java.util.Optional;

public interface ProfileRepository {
    Optional<UserProfile> findByUserId(int userId);
    List<UserProfile> findAll();
    UserProfile save(UserProfile userProfile); 
    void delete(UserProfile userProfile);   
}
