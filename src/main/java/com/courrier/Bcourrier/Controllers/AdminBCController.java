package com.courrier.Bcourrier.Controllers;

import com.courrier.Bcourrier.DTO.AdminBC.AdminBCDashboardDTO;
import com.courrier.Bcourrier.Entities.Courrier;
import com.courrier.Bcourrier.Entities.ServiceIntern;
import com.courrier.Bcourrier.Enums.Confidentialite;
import com.courrier.Bcourrier.Enums.Urgence;
import com.courrier.Bcourrier.Enums.VoieExpedition;
import com.courrier.Bcourrier.Repositories.AdminBcRepository;
import com.courrier.Bcourrier.Repositories.ServiceInternRepository;
import com.courrier.Bcourrier.Services.AdminBcService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    private final AdminBcService courrierService;
    private final ServiceInternRepository serviceInternRepository;
    @Autowired
    private final AdminBcService adminBcService;
    @Autowired
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

    fein
    ecc

}