package com.courrier.Bcourrier.Controllers;

import com.courrier.Bcourrier.DTO.AdminBC.AdminBCDashboardDTO;
import com.courrier.Bcourrier.DTO.AdminBC.CourrierArriveeDTO;
import com.courrier.Bcourrier.DTO.AdminBC.CourrierDepartDTO;
import com.courrier.Bcourrier.DTO.AdminBC.StatsDTO;
import com.courrier.Bcourrier.DTO.Profile.ChangePasswordDTO;
import com.courrier.Bcourrier.DTO.Profile.PersonalInfoDTO;
import com.courrier.Bcourrier.DTO.Profile.PreferencesDTO;
import com.courrier.Bcourrier.Entities.Courrier;
import com.courrier.Bcourrier.Entities.Employe;
import com.courrier.Bcourrier.Entities.ServiceIntern;
import com.courrier.Bcourrier.Enums.Confidentialite;
import com.courrier.Bcourrier.Enums.Urgence;
import com.courrier.Bcourrier.Enums.VoieExpedition;
import com.courrier.Bcourrier.Repositories.AdminBcRepository;
import com.courrier.Bcourrier.Repositories.EmployeRepository;
import com.courrier.Bcourrier.Repositories.ServiceInternRepository;
import com.courrier.Bcourrier.Services.AdminBcService;
import com.courrier.Bcourrier.Services.ProfilService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import org.springframework.http.HttpStatus;
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
    private final ProfilService profilService;
    private final AdminBcRepository adminBcRepository;

    @GetMapping("/dashboard")
    public AdminBCDashboardDTO getDashboardData() {
        return adminBcService.getAdminDashboardData();
    }


    @PostMapping("/admin/courriers/arrivee")
    public ResponseEntity<String> enregistrerCourrierArrivee(
            @RequestParam("signataire") String signataire,
            @RequestParam("objet") String objet,
            @RequestParam("description") String description,
            @RequestParam("numeroRegistre") int numeroRegistre,
            @RequestParam("degreConfidentialite") Confidentialite degreConfidentialite,
            @RequestParam("urgence") Urgence urgence,
            @RequestParam("service") Long serviceId,
            @RequestParam("attachment") MultipartFile file) {

        try {
            courrierService.enregistrerCourrierArrivee(
                    signataire, objet, description, numeroRegistre,
                    degreConfidentialite, urgence, serviceId, file
            );
            return ResponseEntity.ok("Courrier enregistré avec succès");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'enregistrement");
        }
    }

    @PostMapping("/admin/courriers/depart")
    public ResponseEntity<String> enregistrerCourrierDepart(
            @RequestParam("objet") String objet,
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
                    objet, description, degreConfidentialite, urgence, serviceId, attachment,nomExpediteur, voieExpedition
            );
            return ResponseEntity.ok("Départ enregistré avec succès");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'enregistrement du départ");
        }
    }


    @GetMapping("/courriers/arrivees")
    public ResponseEntity<List<CourrierArriveeDTO>> getArriveeCourriers() {
        return ResponseEntity.ok(adminBcService.getAllArriveeCourrierDTOs());
    }

    @GetMapping("/courriers/departs")
    public ResponseEntity<List<CourrierDepartDTO>> getDepartCourriers() {
        return ResponseEntity.ok(adminBcService.getAllDepartCourrierDTOs());
    }

    @GetMapping("/stats")
    public ResponseEntity<StatsDTO> getStats() {
        return ResponseEntity.ok(adminBcService.getStats());
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
