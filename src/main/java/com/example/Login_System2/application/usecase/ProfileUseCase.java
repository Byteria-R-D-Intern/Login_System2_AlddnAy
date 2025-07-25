package com.example.Login_System2.application.usecase;

import com.example.Login_System2.domain.port.ProfileRepository;
import com.example.Login_System2.domain.port.UserRepository;
import com.example.Login_System2.domain.model.User;
import com.example.Login_System2.domain.model.UserProfile;
import com.example.Login_System2.api.dto.ProfileRequest;
import com.example.Login_System2.api.dto.ProfileResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class ProfileUseCase {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    public ProfileUseCase(ProfileRepository profileRepository, UserRepository userRepository) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
    }

    private ProfileResponse mapToResponse(UserProfile profile) {
        ProfileResponse response = new ProfileResponse();
        response.setId(profile.getId());
        response.setAdres(profile.getAdres());
        response.setTelefon(profile.getTelefon());
        response.setBirthDate(profile.getBirthDate());
        return response;
    }

    public ProfileResponse createProfile(ProfileRequest request, int currentUserId, String currentUserRole) {
        if(currentUserRole.equals("ROLE_USER")){
            // Kullanıcı sadece kendi profili için işlem yapabilir
            // userId artık request'ten gelmiyor, doğrudan currentUserId kullanılacak
        }
        else if(!currentUserRole.equals("ROLE_ADMIN")){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bu işlemi yapmaya yetkiniz yok.");
        }

        User user = userRepository.findById(currentUserId)
        .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı."));

        UserProfile userProfile = new UserProfile();
        userProfile.setUser(user);
        userProfile.setAdres(request.getAdres());
        userProfile.setTelefon(request.getTelefon());
        userProfile.setBirthDate(request.getBirthDate());
        
        UserProfile savedProfile = profileRepository.save(userProfile);
        return mapToResponse(savedProfile);
        
    }

    public ProfileResponse getProfileById(int id, int currentUserId, String currentUserRole) {
        UserProfile profile = profileRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profil bulunamadı."));
        if (currentUserRole.equals("ROLE_USER") && profile.getUser().getId() != currentUserId) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Kendi dışınızda profil görüntüleyemezsiniz.");
        }
        return mapToResponse(profile);
    }

    public ProfileResponse updateProfile(int id, ProfileRequest request, int currentUserId, String currentUserRole) {
        UserProfile profile = profileRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profil bulunamadı."));
        if (currentUserRole.equals("ROLE_USER") && profile.getUser().getId() != currentUserId) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Kendi dışınızda profil güncelleyemezsiniz.");
        }
        profile.setAdres(request.getAdres());
        profile.setTelefon(request.getTelefon());
        profile.setBirthDate(request.getBirthDate());
        UserProfile updated = profileRepository.save(profile);
        return mapToResponse(updated);
    }

    public void deleteProfile(int id, int currentUserId, String currentUserRole) {
        UserProfile profile = profileRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profil bulunamadı."));
        if (currentUserRole.equals("ROLE_USER") && profile.getUser().getId() != currentUserId) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Kendi dışınızda profil silemezsiniz.");
        }
    }

    public ProfileResponse getProfileByUserId(int userId, int currentUserId, String currentUserRole) {
        UserProfile profile = profileRepository.findByUserId(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profil bulunamadı."));
        if (currentUserRole.equals("ROLE_USER") && userId != currentUserId) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Kendi dışınızda profil görüntüleyemezsiniz.");
        }
        return mapToResponse(profile);
    }

    public ProfileResponse updateProfileByUserId(int userId, ProfileRequest request, int currentUserId, String currentUserRole) {
        UserProfile profile = profileRepository.findByUserId(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profil bulunamadı."));
        if (currentUserRole.equals("ROLE_USER") && userId != currentUserId) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Kendi dışınızda profil güncelleyemezsiniz.");
        }
        profile.setAdres(request.getAdres());
        profile.setTelefon(request.getTelefon());
        profile.setBirthDate(request.getBirthDate());
        UserProfile updated = profileRepository.save(profile);
        return mapToResponse(updated);
    }

    public void deleteProfileByUserId(int userId, int currentUserId, String currentUserRole) {
        UserProfile profile = profileRepository.findByUserId(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profil bulunamadı."));
        if (currentUserRole.equals("ROLE_USER") && userId != currentUserId) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Kendi dışınızda profil silemezsiniz.");
        }
        profileRepository.deleteById(profile.getId());
    }

    public java.util.List<ProfileResponse> getAllProfiles(String currentUserRole) {
        if (!currentUserRole.equals("ROLE_ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tüm profilleri sadece admin görebilir.");
        }
        java.util.List<UserProfile> profiles = profileRepository.findAll();
        return profiles.stream().map(this::mapToResponse).toList();
    }
}
