package com.example.Login_System2.infrastructure.repository;

import com.example.Login_System2.domain.model.UserProfile;
import com.example.Login_System2.domain.port.ProfileRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public  interface JpaProfile extends JpaRepository<UserProfile, Integer>, ProfileRepository {
    Optional<UserProfile> findByUserId(Integer userId);
    UserProfile save (int userId);
}
