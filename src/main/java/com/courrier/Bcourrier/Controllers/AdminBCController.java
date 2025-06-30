package com.courrier.Bcourrier.Controllers;

import com.courrier.Bcourrier.DTO.AdminBC.*;
import com.courrier.Bcourrier.Entities.Confidentialite;
import com.courrier.Bcourrier.Entities.Courrier;
import com.courrier.Bcourrier.Entities.Urgence;
import com.courrier.Bcourrier.Entities.VoieExpedition;
import com.courrier.Bcourrier.Enums.TypeCourrier;
import com.courrier.Bcourrier.Repositories.*;
import com.courrier.Bcourrier.Services.AdminBcService;
import com.courrier.Bcourrier.Services.AdminSIService;
import com.courrier.Bcourrier.Services.ProfilService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;

@Data
@RestController
@RequestMapping("/api/admin-bc")
@RequiredArgsConstructor

public class AdminBCController {
    @Autowired
    private final AdminBcService courrierService;
    private final ServiceInternRepository serviceInternRepository;
    private final VoieRepository voieRepository;
    private final AdminBcService adminBcService;
    private final AdminSIService adminSIService;
    private final UrgenceRepository urgenceRepository;
    private final ConfidentialiteRepository confidentialiteRepository;
    private final ProfilService profilService;
    private final EmployeRepository employeRepository;
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
            @RequestParam("dateArrive") LocalDate dateArrive,
            @RequestParam("dateEnregistre") LocalDate dateEnregistre,
            @RequestParam(name = "reponseAId", required = false) Integer reponseAId,
            @RequestParam(name = "reponseAId", required = false) Integer employe,
            @RequestParam("attachment") MultipartFile file) {

        try {
            courrierService.enregistrerCourrierArrivee(
                                signataire,nature, objet, description, numeroRegistre,
                                degreConfidentialite, urgence, dateArrive, dateEnregistre, reponseAId, serviceId,employe, file
                        );
            return ResponseEntity.ok("Courrier enregistré avec succès");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'enregistrement");
        }
    }

    @GetMapping(value = {"/admin/courriers/arrivee", "/admin/courriers/depart", "/courrier/employe"})
    public AjouterDTO getStaticOptions() {
        AjouterDTO dto = new AjouterDTO();

        dto.setNumRegister(courrierRepository.listCourrier());
        dto.setUrgences(urgenceRepository.findAll());
        dto.setConfidentialites(confidentialiteRepository.findAll());
        dto.setServices(serviceInternRepository.findAll());

        List<EmployeDTO> employeDTOs = employeRepository.findAll().stream()
                .map(e -> new EmployeDTO(e.getId(), e.getNom(), e.getPrenom()))
                .collect(Collectors.toList());

        dto.setEmployes(employeDTOs);

        return dto;
    }
//    @GetMapping("/consulter-courrier/employe")
//    public List<ConsulterCourrierEmployeDTO> getCourrierEmploye() {
//        List<Courrier> courriers = courrierRepository.findByType(TypeCourrier.EMPLOYE);
//
//        return courriers.stream()
//                .map(c -> new ConsulterCourrierEmployeDTO(
//                        c.getId(),
//                        c.getDateRegistre(),
//                        c.getObject(),
//                        c.getEmploye().getNom(),
//                        c.getEmploye().getPrenom(),
//                        c.getEmploye().getCin()
//                ))
//                .collect(Collectors.toList());
//    }


//    @PostMapping("/courrier/employe")
//    public ResponseEntity<String> enregistrerCourrierEmploye(
//            @RequestParam("objet") String objet,
//            @RequestParam("description") String description,
//            @RequestParam("numeroRegistre") int numeroRegistre,
//            @RequestParam("employeId") Long employeId,
//            @RequestParam("attachment") MultipartFile attachment,
//            @RequestParam("dateArrive") LocalDate dateArrive,
//            @RequestParam("dateEnregistre") LocalDate dateEnregistre,
//            @RequestParam(name = "reponseAId", required = false) Integer reponseAId // 👈 NEW parameter
//
//    ) {
//        try {
//            adminBcService.enregistrerCourrierEmploye(
//                    objet,
//                    description,
//                    numeroRegistre,
//                    employeId,
//                    attachment,
//                    dateArrive,dateEnregistre,reponseAId
//            );
//            return ResponseEntity.ok("Courrier enregistré avec succès.");
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
//        } catch (IOException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de l’enregistrement du fichier.");
//        }
//    }

    @PostMapping("/admin/courriers/depart")
    public ResponseEntity<String> enregistrerCourrierDepart(
            @RequestParam("objet") String objet,
            @RequestParam("nature") String nature,
            @RequestParam("description") String description,
            @RequestParam("numeroRegistre") int numeroRegistre,
            @RequestParam("degreConfidentialite") Confidentialite degreConfidentialite,
            @RequestParam("urgence") Urgence urgence,
            @RequestParam("service") Long serviceId,
            @RequestParam("attachment") MultipartFile attachment,
            @RequestParam("nomExpediteur") String nomExpediteur,
            @RequestParam("voieExpedition") VoieExpedition voieExpedition,
            @RequestParam("dateArrive") LocalDate dateDepart,
            @RequestParam("dateEnregistre") LocalDate dateEnregistre,
            @RequestParam(name = "reponseAId", required = false) Integer reponseAId // 👈 NEW parameter


    ) {


        try {
            courrierService.enregistrerCourrierDepart(
                    objet,nature, description, degreConfidentialite, urgence, numeroRegistre,
                    serviceId, attachment,nomExpediteur, voieExpedition,dateDepart,dateEnregistre,reponseAId
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




    @GetMapping("/api/courriers/{id}/download")
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
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + file.getFileName().toString() + "\"")
                    .body(resource);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to download file: " + e.getMessage());
        }
    }

    @GetMapping("/api/courriers/{id}/view-pdf")
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

    @GetMapping("/support")
    public ResponseEntity<InputStreamResource> viewPdf() {
        try {
            ClassPathResource pdfFile = new ClassPathResource("support/ADMINBC.pdf");

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

    // --- VOIE ---
    @GetMapping("/Voie")
    public List<Urgence> getAllVoie() {
        return adminSIService.getAllUrgences();
    }

    @PostMapping("/Voie")
    public ResponseEntity<String> addVoie(@RequestBody Urgence u) {
        boolean ok = adminSIService.addUrgence(u);
        return ok ? ResponseEntity.ok("Urgence ajoutée")
                : ResponseEntity.status(400).body("Erreur ajout urgence");
    }

    @GetMapping("/urgences")
    public List<Urgence> getUrgences() {
        return adminSIService.getAllUrgences();
    }

    @PostMapping("/urgences")
    public ResponseEntity<String> addUrgence(@RequestBody Urgence u) {
        boolean ok = adminSIService.addUrgence(u);
        return ok ? ResponseEntity.ok("Urgence ajoutée")
                : ResponseEntity.status(400).body("Erreur ajout urgence");
    }

    @GetMapping("/confidentialites")
    public List<Confidentialite> getConfidentialites() {
        return adminSIService.getAllConfidentialites();
    }

    @PostMapping("/confidentialites")
    public ResponseEntity<String> addConfidentialite(@RequestBody Confidentialite c) {
        boolean ok = adminSIService.addConfidentialite(c);
        return ok ? ResponseEntity.ok("Confidentialité ajoutée")
                : ResponseEntity.status(400).body("Erreur ajout confidentialité");
    }
    @PutMapping("/delete/urgence/{id}")
    public ResponseEntity<?> softDeleteUrgence(@PathVariable Long id) {
        return urgenceRepository.findById(id).map(urgence -> {
            urgence.setDateSuppression(LocalDateTime.now());
            urgenceRepository.save(urgence);
            return ResponseEntity.ok("Marked as deleted");
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/delete/confidentialite/{id}")
    public ResponseEntity<?> softDeleteConfidentialite(@PathVariable Long id) {
        return confidentialiteRepository.findById(id).map(conf -> {
            conf.setDateSuppression(LocalDateTime.now());
            confidentialiteRepository.save(conf);
            return ResponseEntity.ok("Marked as deleted (dateImpression set)");
        }).orElse(ResponseEntity.notFound().build());
    }
    @PutMapping("/urgence/restore/{id}")
    public ResponseEntity<?> restoreurgence(@PathVariable Long id) {
        return urgenceRepository.findById(id).map(svc -> {
            svc.setDateSuppression(null);
            urgenceRepository.save(svc);
            return ResponseEntity.ok("restoré");
        }).orElse(ResponseEntity.notFound().build());
    }
    @PutMapping("/confidentialite/restore/{id}")
    public ResponseEntity<?> restoreconfidentialite(@PathVariable Long id) {
        return confidentialiteRepository.findById(id).map(svc -> {
            svc.setDateSuppression(null);
            confidentialiteRepository.save(svc);
            return ResponseEntity.ok("restoré");
        }).orElse(ResponseEntity.notFound().build());
    }
    @PutMapping("/urgence/update/{id}")
    public ResponseEntity<?> updateurgence(@PathVariable Long id, @RequestParam String nom) {
        return urgenceRepository.findById(id).map(svc -> {
            svc.setNom(nom);
            urgenceRepository.save(svc);
            return ResponseEntity.ok("modifié");
        }).orElse(ResponseEntity.notFound().build());
    }
    @PutMapping("/confidentialite/update/{id}")
    public ResponseEntity<?> updateconfidentialite(@PathVariable Long id, @RequestParam String nom) {
        return confidentialiteRepository.findById(id).map(svc -> {
            svc.setNom(nom);
            confidentialiteRepository.save(svc);
            return ResponseEntity.ok("modifié");
        }).orElse(ResponseEntity.notFound().build());
    }
    @PutMapping("/voiexpedition/restore/{id}")
    public ResponseEntity<?> restorevoie(@PathVariable Long id) {
        return voieRepository.findById(id).map(svc -> {
            svc.setDateSuppression(null);
            voieRepository.save(svc);
            return ResponseEntity.ok("restoré");
        }).orElse(ResponseEntity.notFound().build());
    }
    @PutMapping("/voiexpedition/update/{id}")
    public ResponseEntity<?> updatevoie(@PathVariable Long id,@PathVariable String nom) {
        return voieRepository.findById(id).map(svc -> {
            svc.setNom(nom);
            voieRepository.save(svc);
            return ResponseEntity.ok("restoré");
        }).orElse(ResponseEntity.notFound().build());
    }
    @PutMapping("/voiexpedition/delete/{id}")
    public ResponseEntity<?> deletevoie(@PathVariable Long id) {
        return voieRepository.findById(id).map(svc -> {
            svc.setDateSuppression(LocalDateTime.now());
            voieRepository.save(svc);
            return ResponseEntity.ok("restoré");
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/courrier/archiver/{id}")
    public ResponseEntity<?> archiverCourrier(@PathVariable int id) {
        return courrierRepository.findById(id).map(svc -> {
            svc.setArchiver(true);
            courrierRepository.save(svc);
            return ResponseEntity.ok("archivé");
        }).orElse(ResponseEntity.notFound().build());
    }





}
