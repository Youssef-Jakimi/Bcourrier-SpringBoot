package com.courrier.Bcourrier.Controllers;

import com.courrier.Bcourrier.DTO.AdminBC.*;
import com.courrier.Bcourrier.DTO.Profile.ChangePasswordDTO;
import com.courrier.Bcourrier.DTO.Profile.PersonalInfoDTO;
import com.courrier.Bcourrier.DTO.Profile.PreferencesDTO;
import com.courrier.Bcourrier.Entities.Courrier;
import com.courrier.Bcourrier.Entities.Employe;
import com.courrier.Bcourrier.Entities.ServiceIntern;
import com.courrier.Bcourrier.Enums.Confidentialite;
import com.courrier.Bcourrier.Enums.Urgence;
import com.courrier.Bcourrier.Enums.VoieExpedition;
import com.courrier.Bcourrier.Repositories.*;
import com.courrier.Bcourrier.Services.AdminBcService;
import com.courrier.Bcourrier.Services.ProfilService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Data
@RestController
@RequestMapping("/api/admin-bc")
@RequiredArgsConstructor
public class AdminBCController {
    @Autowired
    private final AdminBcService courrierService;
    private final ServiceInternRepository serviceInternRepository;
    private final AdminBcService adminBcService;
    private final UrgenceRepository urgenceRepository;
    private final ConfidentialitéRepository confidentialitéRepository;
    private final ProfilService profilService;
    private final AdminBcRepository adminBcRepository;
    private final CourrierRepository courrierRepository;

    @GetMapping("/dashboard")
    public AdminBCDashboardDTO getDashboardData() {
        return adminBcService.getAdminDashboardData();
    }


    @PostMapping("/admin/courriers/arrivee")
    public ResponseEntity<String> enregistrerCourrierArrivee(
            @RequestParam("signataire") String signataire,
            @RequestParam("nature") String nature,
            @RequestParam("objet") String objet,
            @RequestParam("description") String description,
            @RequestParam("numeroRegistre") int numeroRegistre,
            @RequestParam("degreConfidentialite") Confidentialite degreConfidentialite,
            @RequestParam("urgence") Urgence urgence,
            @RequestParam("service") Long serviceId,
            @RequestParam("attachment") MultipartFile file) {

        try {
            courrierService.enregistrerCourrierArrivee(
                    signataire,nature, objet, description, numeroRegistre,
                    degreConfidentialite, urgence, serviceId, file
            );
            return ResponseEntity.ok("Courrier enregistré avec succès");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'enregistrement");
        }
    }

    @GetMapping(value = {"/admin/courriers/arrivee", "/admin/courriers/depart"})
    public AjouterDTO getStaticOptions() {
        AjouterDTO dto = new AjouterDTO();
        dto.setUrgences(urgenceRepository.findAll());
        dto.setConfidentialites(confidentialitéRepository.findAll());
        dto.setServices(serviceInternRepository.findAll());
        return dto;
    }


    @PostMapping("/admin/courriers/depart")
    public ResponseEntity<String> enregistrerCourrierDepart(
            @RequestParam("objet") String objet,
            @RequestParam("nature") String nature,
            @RequestParam("description") String description,
            @RequestParam("degreConfidentialite") Confidentialite degreConfidentialite,
            @RequestParam("urgence") Urgence urgence,
            @RequestParam("service") Long serviceId,
            @RequestParam("attachment") MultipartFile attachment,
            @RequestParam("nomExpediteur") String nomExpediteur,
            @RequestParam("voieExpedition") VoieExpedition voieExpedition
    ) {


        try {
            courrierService.enregistrerCourrierDepart(
                    objet,nature, description, degreConfidentialite, urgence, serviceId, attachment,nomExpediteur, voieExpedition
            );
            return ResponseEntity.ok("Départ enregistré avec succès");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'enregistrement du départ");
        }
    }


    @GetMapping("/courriers/arrivees")
    public CourrierArriveeResponseDTO getArrivees() {
        return adminBcService.getAllArriveeCourrierDTOs();
    }

    @GetMapping("/courriers/departs")
    public CourrierDepartResponseDTO getDeparts() {
        return adminBcService.getAllDepartCourrierDTOs();
    }


    @GetMapping("/stats")
    public ResponseEntity<StatsDTO> getStats() {
        return ResponseEntity.ok(adminBcService.getStats());
    }


    @PostMapping("/profile/update-personal")
    public ResponseEntity<String> updatePersonalInfo(
            @RequestBody PersonalInfoDTO dto,
            Principal principal) {
        String login = principal.getName();
        boolean ok = profilService.updatePersonalInfo(login, dto);
        return ok ? ResponseEntity.ok("Personal info updated")
                : ResponseEntity.status(400).body("Failed to update");
    }



    @PostMapping("/profile/change-password")
    public ResponseEntity<String> changePassword(
            @RequestBody ChangePasswordDTO dto,
            Principal principal) {
        String login = principal.getName();
        boolean ok = profilService.changePassword(login, dto);
        return ok ? ResponseEntity.ok("Password updated")
                : ResponseEntity.status(400).body("Current password incorrect");
    }

    @PostMapping("/profile/update-preferences")
    public ResponseEntity<String> updatePreferences(
            @RequestBody PreferencesDTO dto,
            Principal principal) {
        String login = principal.getName();
        boolean ok = profilService.updatePreferences(login, dto);
        return ok ? ResponseEntity.ok("Preferences updated")
                : ResponseEntity.status(400).body("Failed to update preferences");
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

    @PostMapping("/courriers/update")
    public ResponseEntity<String> adminBCUpdateCourrier(@RequestBody AdminBCUpdateCourrierDTO dto) {
        boolean ok = adminBcService.adminBCUpdateCourrier(dto);
        return ok ? ResponseEntity.ok("Courrier mis à jour")
                : ResponseEntity.status(400).body("Mise à jour échouée");
    }


}
