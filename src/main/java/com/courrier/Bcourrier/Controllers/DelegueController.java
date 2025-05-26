package com.courrier.Bcourrier.Controllers;

import com.courrier.Bcourrier.DTO.AdminBC.AdminBCDashboardDTO;
import com.courrier.Bcourrier.DTO.AdminBC.CourrierArriveeDTO;
import com.courrier.Bcourrier.DTO.AdminBC.CourrierDepartDTO;
import com.courrier.Bcourrier.DTO.AdminBC.StatsDTO;
import com.courrier.Bcourrier.DTO.Profile.ChangePasswordDTO;
import com.courrier.Bcourrier.DTO.Profile.PersonalInfoDTO;
import com.courrier.Bcourrier.DTO.Profile.PreferencesDTO;
import com.courrier.Bcourrier.Entities.Courrier;
import com.courrier.Bcourrier.Repositories.CourrierRepository;
import com.courrier.Bcourrier.Services.AdminBcService;
import com.courrier.Bcourrier.Services.DelegueService;
import com.courrier.Bcourrier.Services.ProfilService;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
@RequestMapping("/api/delegue")
@RestController
@Data
@RequiredArgsConstructor
public class DelegueController {
    private final DelegueService delegueService;
    private final ProfilService profilService;
    private final CourrierRepository courrierRepository;

    @GetMapping("/dashboard")
    public AdminBCDashboardDTO getDashboardData() {
        return delegueService.getAdminDashboardData();
    }

    @GetMapping("/courriers/arrivees")
    public ResponseEntity<List<CourrierArriveeDTO>> getArriveeCourriers() {
        return ResponseEntity.ok(delegueService.getAllArriveeCourrierDTOs());
    }
    @GetMapping("/api/courriers/{id}/download")
    public ResponseEntity<org.springframework.core.io.Resource> downloadAttachment(@PathVariable Long id) {
        Courrier courrier = courrierRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        String attachmentPath = courrier.getAttachmentPath();
        if (attachmentPath == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No attachment for this courrier.");
        }
        try {
            Path file = Paths.get(attachmentPath).toAbsolutePath().normalize();
            if (!java.nio.file.Files.exists(file)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File does not exist.");
            }
            org.springframework.core.io.Resource resource = new UrlResource(file.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found or not readable.");
            }
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + file.getFileName().toString() + "\"")
                    .body(resource);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to download file: " + e.getMessage());
        }
    }

    @GetMapping("/api/courriers/{id}/view-pdf")
    public ResponseEntity<Resource> viewPdfInline(@PathVariable Long id) {
        Courrier courrier = courrierRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        String attachmentPath = courrier.getAttachmentPath();
        if (attachmentPath == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No attachment for this courrier.");
        }
        try {
            Path file = Paths.get(attachmentPath).toAbsolutePath().normalize();
            if (!java.nio.file.Files.exists(file)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File does not exist.");
            }
            Resource resource = new UrlResource(file.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found or not readable.");
            }
            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "inline; filename=\"" + file.getFileName().toString() + "\"")
                    .body(resource);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to view file: " + e.getMessage());
        }
    }


    @GetMapping("/courriers/departs")
    public ResponseEntity<List<CourrierDepartDTO>> getDepartCourriers() {
        return ResponseEntity.ok(delegueService.getAllDepartCourrierDTOs());
    }


    @GetMapping("/stats")
    public ResponseEntity<StatsDTO> getStats() {
        return ResponseEntity.ok(delegueService.getStats());
    }


    @PostMapping("/profile/update-personal")
    public ResponseEntity<String> updatePersonalInfo(
            @RequestBody PersonalInfoDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        String login = userDetails.getUsername();
        boolean ok = profilService.updatePersonalInfo(login, dto);
        return ok ? ResponseEntity.ok("Personal info updated")
                : ResponseEntity.status(400).body("Failed to update");
    }

    @PostMapping("/profile/change-password")
    public ResponseEntity<String> changePassword(
            @RequestBody ChangePasswordDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        String login = userDetails.getUsername();
        boolean ok = profilService.changePassword(login, dto);
        return ok ? ResponseEntity.ok("Password updated")
                : ResponseEntity.status(400).body("Current password incorrect");
    }

    @PostMapping("/profile/update-preferences")
    public ResponseEntity<String> updatePreferences(
            @RequestBody PreferencesDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        String login = userDetails.getUsername();
        boolean ok = profilService.updatePreferences(login, dto);
        return ok ? ResponseEntity.ok("Preferences updated")
                : ResponseEntity.status(400).body("Failed to update preferences");
    }
}
