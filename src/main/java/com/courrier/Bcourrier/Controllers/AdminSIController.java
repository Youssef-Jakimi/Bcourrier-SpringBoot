package com.courrier.Bcourrier.Controllers;

import com.courrier.Bcourrier.DTO.AdminSI.AdminSICreateServiceDTO;
import com.courrier.Bcourrier.DTO.AdminSI.AdminSIDashboardDTO;
import com.courrier.Bcourrier.DTO.AdminSI.AdminSIModifyUserDTO;
import com.courrier.Bcourrier.DTO.AdminSI.AdminSIUserDTO;
import com.courrier.Bcourrier.Entities.Confidentialite;
import com.courrier.Bcourrier.Entities.Role;
import com.courrier.Bcourrier.Entities.ServiceIntern;
import com.courrier.Bcourrier.Entities.Urgence;
import com.courrier.Bcourrier.Repositories.ConfidentialiteRepository;
import com.courrier.Bcourrier.Repositories.RoleRepository;
import com.courrier.Bcourrier.Repositories.ServiceInternRepository;
import com.courrier.Bcourrier.Repositories.UrgenceRepository;
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
    private final UrgenceRepository UrgenceRepository;

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
        return UrgenceRepository.findById(id).map(urgence -> {
            urgence.setDateSuppression(LocalDateTime.now());
            UrgenceRepository.save(urgence);
            return ResponseEntity.ok("Marked as deleted (dateImpression set)");
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
