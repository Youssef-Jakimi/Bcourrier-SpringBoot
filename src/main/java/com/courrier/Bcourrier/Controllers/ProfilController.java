package com.courrier.Bcourrier.Controllers;

import com.courrier.Bcourrier.DTO.Profile.ChangePasswordDTO;
import com.courrier.Bcourrier.DTO.Profile.PersonalInfoDTO;
import com.courrier.Bcourrier.DTO.Profile.PreferencesDTO;
import com.courrier.Bcourrier.Services.ProfilService;
import io.jsonwebtoken.Jwt;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Data
@RestController
@RequiredArgsConstructor
public class ProfilController {

    @Autowired
    private final  ProfilService profilService;


    @PostMapping({
            "/api/admin-bc/profil/update-personal",
            "/api/admin-si/profil/update-personal",
            "/api/delegue/profil/update-personal",
            "/api/rh/profil/update-personal",
            "/api/responsable-svc/profil/update-personal"
    })
    public ResponseEntity<String> updatePersonalInfo(
            @RequestBody PersonalInfoDTO dto,
            Principal principal) {
        String login = principal.getName();
        boolean ok = profilService.updatePersonalInfo(login, dto);
        return ok ? ResponseEntity.ok("Personal info updated")
                : ResponseEntity.status(400).body("Failed to update");
    }

    @PostMapping({
            "/api/admin-bc/profil/change-password",
            "/api/admin-si/profil/change-password",
            "/api/delegue/profil/change-password",
            "/api/rh/profil/change-password",
            "/api/responsable-svc/profil/change-password"
    })
    public ResponseEntity<String> changePassword(
            @RequestBody ChangePasswordDTO dto,
            Principal principal) {
        String login = principal.getName();
        boolean ok = profilService.changePassword(login, dto);
        return ok ? ResponseEntity.ok("Password updated")
                : ResponseEntity.status(400).body("Current password incorrect");
    }

    @PostMapping({
            "/api/admin-bc/profil/update-preferences",
            "/api/admin-si/profil/update-preferences",
            "/api/delegue/profil/update-preferences",
            "/api/rh/profil/update-preferences",
            "/api/responsable-svc/profil/update-preferences"
    })
    public ResponseEntity<String> updatePreferences(
            @RequestBody PreferencesDTO dto,
            Principal principal) {
        String login = principal.getName();
        boolean ok = profilService.updatePreferences(login, dto);
        return ok ? ResponseEntity.ok("Preferences updated")
                : ResponseEntity.status(400).body("Failed to update preferences");
    }

    @GetMapping({
            "/api/admin-bc/profil/personal-info",
            "/api/admin-si/profil/personal-info",
            "/api/delegue/profil/personal-info",
            "/api/rh/profil/personal-info",
            "/api/responsable-svc/profil/personal-info"
    })
    public ResponseEntity<PersonalInfoDTO> getPersonalInfo(Principal principal) {
        String login = principal.getName();
        return ResponseEntity.ok(profilService.getPersonalInfo(login));
    }

    @GetMapping({
            "/api/admin-bc/profil/preferences",
            "/api/admin-si/profil/preferences",
            "/api/delegue/profil/preferences",
            "/api/rh/profil/preferences",
            "/api/responsable-svc/profil/preferences"
    })
    public ResponseEntity<PreferencesDTO> getPreferences(Principal principal) {
        String login = principal.getName();
        return ResponseEntity.ok(profilService.getPreferences(login));
    }
}
