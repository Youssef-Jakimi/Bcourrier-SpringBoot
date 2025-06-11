package com.courrier.Bcourrier.Controllers;

import com.courrier.Bcourrier.DTO.AdminSI.AdminSICreateServiceDTO;
import com.courrier.Bcourrier.DTO.AdminSI.AdminSIDashboardDTO;
import com.courrier.Bcourrier.DTO.AdminSI.AdminSIModifyUserDTO;
import com.courrier.Bcourrier.DTO.AdminSI.AdminSIUserDTO;
import com.courrier.Bcourrier.Entities.Confidentialité;
import com.courrier.Bcourrier.Entities.Role;
import com.courrier.Bcourrier.Entities.ServiceIntern;
import com.courrier.Bcourrier.Entities.Urgence;
import com.courrier.Bcourrier.Repositories.ConfidentialitéRepository;
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
import java.util.List;

@RestController
@RequestMapping("/api/admin-si")
@RequiredArgsConstructor
public class AdminSIController {

    @Autowired
    private AdminSIService adminSIService;
    private final ServiceInternRepository serviceInternRepository;
    private final ConfidentialitéRepository confidentialiteRepository;
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
    public List<Confidentialité> getConfidentialites() {
        return adminSIService.getAllConfidentialites();
    }

    @PostMapping("/confidentialites")
    public ResponseEntity<String> addConfidentialite(@RequestBody Confidentialité c) {
        boolean ok = adminSIService.addConfidentialite(c);
        return ok ? ResponseEntity.ok("Confidentialité ajoutée")
                : ResponseEntity.status(400).body("Erreur ajout confidentialité");
    }

    @DeleteMapping("/service/{id}")
    public ResponseEntity<?> deleteServiceIntern(@PathVariable Long id) {
        serviceInternRepository.deleteById(id);
        return ResponseEntity.ok().body("Deleted successfully");
    }

    @DeleteMapping("/delete/urgence/{id}")
    public ResponseEntity<?> deleteurgence(@PathVariable Long id) {
        UrgenceRepository.deleteById(id);
        return ResponseEntity.ok().body("Deleted successfully");
    }

    @DeleteMapping("/delete/role/{id}")
    public ResponseEntity<?> deleterole(@PathVariable Long id) {
        roleRepository.deleteById(id);
        return ResponseEntity.ok().body("Deleted successfully");
    }

    @DeleteMapping("/delete/confidentialite/{id}")
    public ResponseEntity<?> deleteconf(@PathVariable Long id) {
        confidentialiteRepository.deleteById(id);
        return ResponseEntity.ok().body("Deleted successfully");
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
