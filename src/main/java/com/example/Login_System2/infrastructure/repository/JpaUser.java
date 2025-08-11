package com.example.Login_System2.infrastructure.repository;

import com.example.Login_System2.domain.model.User;
import com.example.Login_System2.domain.port.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaUser extends JpaRepository<User, Integer>, UserRepository {
    Optional<User> findByEmail(String email);
    Optional<User> findById(int id);
    List<User> findAll();
    boolean  existsByEmail(String email);
}
