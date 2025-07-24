package com.example.Login_System2.application.usecase;

import com.example.Login_System2.domain.model.User;
import com.example.Login_System2.domain.port.UserRepository;
import com.example.Login_System2.application.service.PasswordHasher;
import java.util.Optional;

public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public AuthenticationService(UserRepository userRepository, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    public Optional<User> authenticate(String email, String rawPassword) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()){
            User kullanici = userOpt.get();

            if (passwordHasher.matches(rawPassword, kullanici.getPassword())) {
                return Optional.of(kullanici);
            }
        }
        return Optional.empty();
    }
}
