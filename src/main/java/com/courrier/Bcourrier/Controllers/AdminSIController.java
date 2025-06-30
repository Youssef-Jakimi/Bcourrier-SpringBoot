package com.courrier.Bcourrier.Controllers;

import com.courrier.Bcourrier.DTO.AdminSI.AdminSICreateServiceDTO;
import com.courrier.Bcourrier.DTO.AdminSI.AdminSIDashboardDTO;
import com.courrier.Bcourrier.DTO.AdminSI.AdminSIModifyUserDTO;
import com.courrier.Bcourrier.DTO.AdminSI.AdminSIUserDTO;
import com.courrier.Bcourrier.Entities.*;
import com.courrier.Bcourrier.Repositories.*;
import com.courrier.Bcourrier.Services.AdminSIService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin-si")
@RequiredArgsConstructor
public class AdminSIController {

    @Autowired
    private AdminSIService adminSIService;
    private final ServiceInternRepository serviceInternRepository;
    private final ConfidentialiteRepository confidentialiteRepository;
    private final RoleRepository roleRepository;
    private final UrgenceRepository urgenceRepository;
    private final VoieRepository voieRepository;
    private final QuestionRepository questionRepository;

    @GetMapping("/dashboard")
    public AdminSIDashboardDTO getDashboard() {
        return adminSIService.getDashboardData();
    }
    @GetMapping("/users/active")
    public List<AdminSIUserDTO> getActiveUsers() {
        return adminSIService.getActiveUsers();
    }

    @GetMapping("/users/to-activate")
    public List<AdminSIUserDTO> getToActivateUsers() {
        return adminSIService.getToActivateUsers();
    }
    // In AdminSIController.java

    @PostMapping("/users/modify")
    public ResponseEntity<String> modifyUser(@RequestBody AdminSIModifyUserDTO dto) {
        boolean ok = adminSIService.modifyUser(dto);
        return ok ? ResponseEntity.ok("Employee updated") : ResponseEntity.status(400).body("Update failed");
    }

    @PostMapping("/users/activate")
    public ResponseEntity<String> activateUser(@RequestBody AdminSIModifyUserDTO dto) {
        boolean ok = adminSIService.activateUser(dto);
        return ok ? ResponseEntity.ok("Employee activated") : ResponseEntity.status(400).body("Activation failed");
    }

    @GetMapping("/services")
    public List<ServiceIntern> getAllServices() {
        return serviceInternRepository.findAll();
    }

    // In AdminSIController.java

    @PostMapping("/services")
    public ResponseEntity<String> addService(@RequestBody AdminSICreateServiceDTO dto) {
        boolean ok = adminSIService.addService(dto);
        return ok ? ResponseEntity.ok("Service créé") : ResponseEntity.status(400).body("Erreur de création");
    }
    // --- URGENCE ---
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
    @GetMapping("/Voie")
    public List<VoieExpedition> getAllVoie() {
        return adminSIService.getAllVoie();
    }

    @PostMapping("/Voie")
    public ResponseEntity<String> addVoie(@RequestBody VoieExpedition u) {
        boolean ok = adminSIService.addVoie(u);
        return ok ? ResponseEntity.ok("Voie ajoutée")
                : ResponseEntity.status(400).body("Erreur ajout Voie expedition");
    }

    // --- ROLE ---
    @GetMapping("/roles")
    public List<Role> getRoles() {
        return adminSIService.getAllRoles();
    }

    @PostMapping("/roles")
    public ResponseEntity<String> addRole(@RequestBody Role r) {
        boolean ok = adminSIService.addRole(r);
        return ok ? ResponseEntity.ok("Role ajouté")
                : ResponseEntity.status(400).body("Erreur ajout role");
    }

    // --- CONFIDENTIALITE ---
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



    @PutMapping("/service/{id}/delete")
    public ResponseEntity<?> softDeleteServiceIntern(@PathVariable Long id) {
        return serviceInternRepository.findById(id).map(service -> {
            service.setDateSuppression(LocalDateTime.now());
            serviceInternRepository.save(service);
            return ResponseEntity.ok("Marked as deleted (dateImpression set)");
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/delete/urgence/{id}")
    public ResponseEntity<?> softDeleteUrgence(@PathVariable Long id) {
        return urgenceRepository.findById(id).map(urgence -> {
            urgence.setDateSuppression(LocalDateTime.now());
            urgenceRepository.save(urgence);
            return ResponseEntity.ok("Marked as deleted");
        }).orElse(ResponseEntity.notFound().build());
    }


    @PutMapping("/delete/role/{id}")
    public ResponseEntity<?> softDeleteRole(@PathVariable Long id) {
        return roleRepository.findById(id).map(role -> {
            role.setDateSuppression(LocalDateTime.now());
            roleRepository.save(role);
            return ResponseEntity.ok("Marked as deleted (dateImpression set)");
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
    @PutMapping("/services/restore/{id}")
    public ResponseEntity<?> restoreservice(@PathVariable Long id) {
        return serviceInternRepository.findById(id).map(svc -> {
            svc.setDateSuppression(null);
            serviceInternRepository.save(svc);
            return ResponseEntity.ok("restauré");
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

    @PutMapping("/role/restore/{id}")
    public ResponseEntity<?> restorerole(@PathVariable Long id) {
        return roleRepository.findById(id).map(svc -> {
            svc.setDateSuppression(null);
            roleRepository.save(svc);
            return ResponseEntity.ok("restoré");
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
    @PutMapping("/confidentialite/restore/{id}")
    public ResponseEntity<?> restoreconfidentialite(@PathVariable Long id) {
        return confidentialiteRepository.findById(id).map(svc -> {
            svc.setDateSuppression(null);
            confidentialiteRepository.save(svc);
            return ResponseEntity.ok("restoré");
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/service/update/{id}")
    public ResponseEntity<?> updateService(@PathVariable Long id, @RequestParam String nom) {
        return serviceInternRepository.findById(id).map(svc -> {
            svc.setNom(nom);
            serviceInternRepository.save(svc);
            return ResponseEntity.ok("modifié");
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


    @PutMapping("/role/update/{id}")
    public ResponseEntity<?> updaterole(@PathVariable Long id, @RequestParam String nom) {
        return roleRepository.findById(id).map(role -> {
            role.setNom(nom);
            roleRepository.save(role);
            return ResponseEntity.ok("modifié");
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/question")
    public List<Question> getQuestion() {
        return adminSIService.getAllQuestion();
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
    @PutMapping("/confidentialite/update/{id}")
    public ResponseEntity<?> updateconfidentialite(@PathVariable Long id, @RequestParam String nom) {
        return confidentialiteRepository.findById(id).map(svc -> {
            svc.setNom(nom);
            confidentialiteRepository.save(svc);
            return ResponseEntity.ok("modifié");
        }).orElse(ResponseEntity.notFound().build());
    }


    @PostMapping("/question/ajouter")
    public boolean ajouterquestion(@PathVariable String question) {
        return adminSIService.addQuestion(question);
    }
    @PutMapping("/question/update/(id)")
    public ResponseEntity<?> updatequestion(@PathVariable Long id,@PathVariable String nom) {
        return questionRepository.findById(id).map(svc -> {
            svc.setNom(nom);
            questionRepository.save(svc);
            return ResponseEntity.ok("modifié");
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/question/delete/{id}")
    public ResponseEntity<?> deletequestion(@PathVariable Long id,@PathVariable String nom) {
        return questionRepository.findById(id).map(svc -> {
            svc.setDateSuppression(LocalDateTime.now());
            questionRepository.save(svc);
            return ResponseEntity.ok("supprimé");
        }).orElse(ResponseEntity.notFound().build());
    }
    @PutMapping("/question/restore/{id}")
    public ResponseEntity<?> restorequestion(@PathVariable Long id,@PathVariable String nom) {
        return questionRepository.findById(id).map(svc -> {
            svc.setDateSuppression(null);
            questionRepository.save(svc);
            return ResponseEntity.ok("modifié");
        }).orElse(ResponseEntity.notFound().build());
    }


    @GetMapping("/support")
    public ResponseEntity<InputStreamResource> viewPdf() {
        try {
            ClassPathResource pdfFile = new ClassPathResource("support/ADMINSI.pdf");

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


}