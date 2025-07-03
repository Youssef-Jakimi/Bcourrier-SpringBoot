package com.courrier.Bcourrier.Controllers;

import com.courrier.Bcourrier.DTO.Profile.ChangePasswordDTO;
import com.courrier.Bcourrier.DTO.Profile.PersonalInfoDTO;
import com.courrier.Bcourrier.DTO.Profile.PreferencesDTO;
import com.courrier.Bcourrier.DTO.ResposableSVC.CourrierSimpleDTO;
import com.courrier.Bcourrier.DTO.ResposableSVC.CourrierStatusUpdateDTO;
import com.courrier.Bcourrier.DTO.ResposableSVC.DashboardSVCDTO;
import com.courrier.Bcourrier.Entities.AffectationCourrierService;
import com.courrier.Bcourrier.Entities.Courrier;
import com.courrier.Bcourrier.Enums.StatutCourrier;
import com.courrier.Bcourrier.Repositories.AffectationCourrierServiceRepository;
import com.courrier.Bcourrier.Repositories.CourrierRepository;
import com.courrier.Bcourrier.Services.ProfilService;
import com.courrier.Bcourrier.Services.ResponsableSVCService;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
@Data
@RestController
@RequestMapping("/api/responsable-svc")
@RequiredArgsConstructor
public class ResponsableSVCController {
    @Autowired
    private final ResponsableSVCService responsableSVCService;
    private final ProfilService profilService;
    private final CourrierRepository courrierRepository;
    private final AffectationCourrierServiceRepository affectationCourrierServiceRepository;


    // DASHBOARD BRIEF
    @GetMapping("/dashboard")
    public DashboardSVCDTO getDashboardBrief(Principal principal) {
        String login = principal.getName();
        return responsableSVCService.getDashboardBrief(login);
    }

    // ARRIVEE EN COURS
    @GetMapping("/courriers/arrivee/encours")
    public List<CourrierSimpleDTO> getArriveeEnCours(Principal principal) {
        String login = principal.getName();
        return responsableSVCService.getArriveeEnCours(login);
    }

    // ARRIVEE ARCHIVES
    @GetMapping("/courriers/arrivee/archives")
    public List<CourrierSimpleDTO> getArriveeArchive(Principal principal) {
        String login = principal.getName();
        return responsableSVCService.getArriveeArchive(login);
    }

    // DEPART EN COURS
    @GetMapping("/courriers/depart/encours")
    public List<CourrierSimpleDTO> getDepartEnCours(Principal principal) {
        String login = principal.getName();
        return responsableSVCService.getDepartEnCours(login);
    }

    // DEPART ARCHIVES
    @GetMapping("/courriers/depart/archives")
    public List<CourrierSimpleDTO> getDepartArchive(Principal principal) {
        String login = principal.getName();
        return responsableSVCService.getDepartArchive(login);
    }

    @PostMapping("/courriers/update-status")
    public ResponseEntity<String> updateCourrierStatus(
            @RequestBody CourrierStatusUpdateDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        String login = userDetails.getUsername();
        boolean ok = responsableSVCService.updateCourrierStatus(login, dto);
        return ok
                ? ResponseEntity.ok("Statut mis à jour")
                : ResponseEntity.status(403).body("Action non autorisée ou courrier introuvable");
    }

    @GetMapping("/support")
    public ResponseEntity<InputStreamResource> viewPdf() {
        try {
            ClassPathResource pdfFile = new ClassPathResource("support/RESSVC.pdf");

            if (!pdfFile.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.inline().filename("ADMINBC.pdf").build());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new InputStreamResource(pdfFile.getInputStream()));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/courriers/{id}/download")
    public ResponseEntity<org.springframework.core.io.Resource> downloadAttachment(@PathVariable int id) {
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
            if(courrier.getStatutCourrier() == StatutCourrier.ENREGISTRE){
                courrier.setStatutCourrier(StatutCourrier.EN_COURS);
                AffectationCourrierService affectation = affectationCourrierServiceRepository.findByCourrier_id(courrier.getId());
                affectation.setDateConsultation(LocalDate.now());
                affectation.setHeureConsultation(LocalTime.now().toString());
                courrierRepository.save(courrier);
                affectationCourrierServiceRepository.save(affectation);
            }
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + file.getFileName().toString() + "\"")
                    .body(resource);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to download file: " + e.getMessage());
        }
    }

    @GetMapping("/courriers/{id}/view-pdf")
    public ResponseEntity<Resource> viewPdfInline(@PathVariable int id) {
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
            if(courrier.getStatutCourrier() == StatutCourrier.ENREGISTRE){
                courrier.setStatutCourrier(StatutCourrier.EN_COURS);
                AffectationCourrierService affectation = affectationCourrierServiceRepository.findByCourrier_id(courrier.getId());
                affectation.setDateConsultation(LocalDate.now());
                affectation.setHeureConsultation(LocalTime.now().toString());
                courrierRepository.save(courrier);
                affectationCourrierServiceRepository.save(affectation);
            }
            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "inline; filename=\"" + file.getFileName().toString() + "\"")
                    .body(resource);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to view file: " + e.getMessage());
        }
    }

}
