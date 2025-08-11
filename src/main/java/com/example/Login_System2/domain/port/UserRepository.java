package com.example.Login_System2.domain.port;

import com.example.Login_System2.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findByEmail(String email);
    Optional<User> findById(int id);
    List<User> findAll();
    User save(User user);
    boolean  existsByEmail(String email);
    void delete(User user);
}
