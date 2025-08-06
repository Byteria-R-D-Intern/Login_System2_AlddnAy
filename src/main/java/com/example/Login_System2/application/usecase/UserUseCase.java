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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
@AllArgsConstructor
@Service
public class UserUseCase {
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final JwtProvider jwtProvider;
    private final Logger log = LoggerFactory.getLogger(UserUseCase.class);

    public Optional<String> login(String email , String password){
        log.info("Kullanıcı kayıt isteği: email={}", email);
        
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
        
        if (!isValidEmail(email)) {
            log.warn("Geçersiz email formatı: {}", email);
            return Optional.empty();
        }
        
        if (!isValidPassword(password)) {
            log.warn("Geçersiz şifre formatı");
            return Optional.empty();
        }
        
        if (name == null || name.trim().isEmpty() || surname == null || surname.trim().isEmpty()) {
            log.warn("Ad veya soyad boş olamaz");
            return Optional.empty();
        }
        
        if (userRepository.existsByEmail(email)) {
            log.warn("Email zaten kullanımda: {}", email);
            return Optional.empty();
        }
        
        
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

        log.info("Kullanıcı başarıyla kaydedildi: userId={}", user.getId());
        return Optional.of(token);

    }

    public Optional<String> updateUser(int requesterId, String requesterRole, int userId, User updateUser) {
        log.info("Kullanıcı güncelleme isteği: requesterId={}, requesterRole={}, userId={}", 
                 requesterId, requesterRole, userId);
    
        // Business Rule: Sadece kendini veya ADMIN başkasını güncelleyebilir
        if (requesterRole.equals("USER") && requesterId != userId) {
            log.warn("USER sadece kendini güncelleyebilir! requesterId={}, userId={}", requesterId, userId);
            return Optional.empty();
        }
    
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            log.warn("Kullanıcı bulunamadı: userId={}", userId);
            return Optional.empty();
        }
        
        User user = userOpt.get();
    
        // Email değişikliği kontrolü
        if (!user.getEmail().equals(updateUser.getEmail())) {
            if (userRepository.existsByEmail(updateUser.getEmail())) {
                log.warn("Email zaten kullanımda: {}", updateUser.getEmail());
                return Optional.empty();
            }
            user.setEmail(updateUser.getEmail());
        }
    
        user.setName(updateUser.getName());
        user.setSurname(updateUser.getSurname());
    
        // Role güncelleme sadece ADMIN tarafından yapılabilir
        if (updateUser.getRole() != null && requesterRole.equals("ADMIN")) {
            user.setRole(updateUser.getRole());
        }
    
        User savedUser = userRepository.save(user);
        log.info("Kullanıcı başarıyla güncellendi: userId={}", userId);
        String token = jwtProvider.generateToken(savedUser.getId(), savedUser.getRole().toString());
        return Optional.of(token);
    }

    public boolean deleteUser(int requesterId, String requesterRole, int userId) {
        log.info("Kullanıcı silme isteği: requesterId={}, requesterRole={}, userId={}", 
                 requesterId, requesterRole, userId);
    
        // Business Rule: Sadece kendini veya ADMIN başkasını silebilir
        if (requesterRole.equals("USER") && requesterId != userId) {
            log.warn("USER sadece kendini silebilir! requesterId={}, userId={}", requesterId, userId);
            return false;
        }
    
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            log.warn("Kullanıcı bulunamadı: userId={}", userId);
            return false;
        }
    
        userRepository.delete(userOpt.get());
        log.info("Kullanıcı başarıyla silindi: userId={}", userId);
        return true;
    }


    //Validasyonlar için yardımcı metotlar
    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    private boolean isValidPassword(String password) {
        return password != null && password.length() >= 8 && 
               password.matches(".*[A-Z].*") && 
               password.matches(".*[a-z].*") && 
               password.matches(".*\\d.*");
    }

    
}
