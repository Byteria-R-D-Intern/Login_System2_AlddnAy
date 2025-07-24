package com.example.Login_System2.application.usecase;

import com.example.Login_System2.domain.model.User;
import com.example.Login_System2.domain.port.UserRepository;
import com.example.Login_System2.application.service.PasswordHasher;
import com.example.Login_System2.application.service.JwtProvider;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Optional;
import com.example.Login_System2.domain.model.Role;

@Data
@AllArgsConstructor
@Service
public class UserUseCase {
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final JwtProvider jwtProvider;

    public Optional<String> login(String email , String password){
        Optional<User> userOpt = userRepository.findByEmail(email);
        if(userOpt.isEmpty()){
            return Optional.empty();
        }
        User user = userOpt.get();
        if(!passwordHasher.matches(password,user.getPassword())){
            return Optional.empty();
        }
        String token = jwtProvider.generateToken(user.getId(), user.getRole().toString());
        return Optional.of(token);
    }

    public Optional<String> register(String email,String name,String surname, String password, String roleStr){
        if(userRepository.existsByEmail(email)){
            return Optional.empty();
        }

        String hashedPassword = passwordHasher.hash(password);
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setSurname(surname);  
        user.setPassword(hashedPassword);

        Role role;
        try{
             role = Role.valueOf(roleStr.toUpperCase());
            
        }catch (IllegalArgumentException e) {
            return Optional.empty(); //geçersiz yol
        }
        user.setRole(role);

        userRepository.save(user);
        String token = jwtProvider.generateToken(user.getId(), user.getRole().toString());
        return Optional.of(token);

    }

    
}
