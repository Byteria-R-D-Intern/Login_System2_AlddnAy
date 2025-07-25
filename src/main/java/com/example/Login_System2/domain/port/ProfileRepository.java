package com.example.Login_System2.domain.port;

import com.example.Login_System2.domain.model.UserProfile;
import java.util.List;
import java.util.Optional;

public interface ProfileRepository {
    Optional<UserProfile> findByUserId(Integer userId);
    Optional<UserProfile> findById(int id);
    UserProfile save(UserProfile profile);
    void deleteById(int id);
    List<UserProfile> findAll();
}
