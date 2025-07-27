package com.example.Login_System2.application.usecase;

import com.example.Login_System2.domain.port.*;
import com.example.Login_System2.domain.model.UserProfile;
import com.example.Login_System2.domain.model.Role;
import com.example.Login_System2.application.service.JwtProvider;

import java.util.Optional;

import org.springframework.stereotype.Service;
 

import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AllArgsConstructor
@Data
@Service
public class ProfileUseCase {
    
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    
    private static final Logger log = LoggerFactory.getLogger(ProfileUseCase.class);

    public Optional<UserProfile> getProfile(int requestId, String requestRole, int targetId) {
        if ("USER".equals(requestRole) && requestId != targetId) {
            return Optional.empty();
        }
        if ("MANAGER".equals(requestRole)) {
            Optional<UserProfile> profileOpt = profileRepository.findByUserId(targetId);
            if (profileOpt.isPresent() && profileOpt.get().getUser().getRole() == Role.ADMIN) {
                return Optional.empty();
            }
        }
        // ADMIN için hiçbir kısıtlama yok!
        return profileRepository.findByUserId(targetId);
    }

    public Optional<UserProfile> updateProfile(int requestId, String requestRole, int targetId, UserProfile updatedProfile) {
        if ("USER".equals(requestRole) && requestId != targetId) {
            return Optional.empty();
        }
        if ("MANAGER".equals(requestRole)) {
            Optional<UserProfile> profileOpt = profileRepository.findByUserId(targetId);
            if (profileOpt.isPresent() && profileOpt.get().getUser().getRole() == Role.ADMIN) {
                return Optional.empty();
            }
        }
        // ADMIN için hiçbir kısıtlama yok!
        Optional<UserProfile> profileOpt = profileRepository.findByUserId(targetId);
        if (profileOpt.isEmpty()) {
            return Optional.empty();
        }
        UserProfile profile = profileOpt.get();
        profile.setAdres(updatedProfile.getAdres());
        profile.setTelefon(updatedProfile.getTelefon());
        profile.setBirthDate(updatedProfile.getBirthDate());
        profileRepository.save(profile);
        return Optional.of(profile);
    }
    
    public boolean deleteProfile(int requesterId, String requesterRole, int targetUserId) {
        if (!requesterRole.equals("ADMIN")) {
            return false; // Sadece admin silebilir
        }
        Optional<UserProfile> profileOpt = profileRepository.findByUserId(targetUserId);
        if (profileOpt.isEmpty()) {
            return false;
        }

         profileRepository.delete(profileOpt.get()); // Eğer silme desteği varsa
        return true;
    }

    public Optional<UserProfile> createProfile(int requesterId, String requesterRole, int targetUserId, UserProfile newProfile) {
        log.info("Profil oluşturma isteği: requesterId={}, requesterRole={}, targetUserId={}", requesterId, requesterRole, targetUserId);

        if ("MANAGER".equals(requesterRole)) {
            log.warn("MANAGER profil oluşturamaz!");
            return Optional.empty();
        }
        
        if ("USER".equals(requesterRole) && requesterId != targetUserId) {
            log.warn("USER sadece kendi profili için oluşturabilir! requesterId={}, targetUserId={}", requesterId, targetUserId);
            return Optional.empty();
        }
        
        if (profileRepository.findByUserId(targetUserId).isPresent()) {
            log.warn("Profil zaten var! targetUserId={}", targetUserId);
            return Optional.empty(); // Zaten profil var
        }
        
        if (userRepository.findById(targetUserId).isEmpty()) {
            log.warn("Kullanıcı bulunamadı! targetUserId={}", targetUserId);
            return Optional.empty(); // Kullanıcı yok
        }
        
        // Profil oluştur
        log.info("Profil oluşturuluyor... targetUserId={}", targetUserId);
        newProfile.setUser(userRepository.findById(targetUserId).get());
        UserProfile savedProfile = profileRepository.save(newProfile);
        log.info("Profil başarıyla oluşturuldu! profileId={}", savedProfile.getId());
        return Optional.of(savedProfile);
    }
}
