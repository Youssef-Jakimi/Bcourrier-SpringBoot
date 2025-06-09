package com.courrier.Bcourrier.Controllers;

import com.courrier.Bcourrier.DTO.AdminBC.ConsulterCourrierEmployeDTO;
import com.courrier.Bcourrier.DTO.Profile.ChangePasswordDTO;
import com.courrier.Bcourrier.DTO.Profile.PersonalInfoDTO;
import com.courrier.Bcourrier.DTO.Profile.PreferencesDTO;
import com.courrier.Bcourrier.DTO.RH.CourrierEmployeeDTO;
import com.courrier.Bcourrier.DTO.RH.EmployeeListDTO;
import com.courrier.Bcourrier.DTO.RH.RhDashboardDTO;
import com.courrier.Bcourrier.Entities.Courrier;
import com.courrier.Bcourrier.Enums.TypeCourrier;
import com.courrier.Bcourrier.Repositories.AffectationCourrierEmployeRepository;
import com.courrier.Bcourrier.Repositories.CourrierRepository;
import com.courrier.Bcourrier.Services.ProfilService;
import com.courrier.Bcourrier.Services.RHService;

import jakarta.annotation.Resource;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.stream.Collectors;

@Data
@RestController
@RequestMapping("/api/RH")
@RequiredArgsConstructor
public class RHController {

    private final RHService rhDashboardService;
    private final ProfilService profilService;

    @GetMapping("/dashboard")
    public RhDashboardDTO getDashboardData() {
        return rhDashboardService.getDashboardData();
    }

    @Autowired
    private RHService employeeService;

    @GetMapping("/employees")
    public List<EmployeeListDTO> getAllEmployees() {
        return employeeService.getAllEmployeeList();
    }


    @Autowired
    private RHService courrierArchiveService;
    @Autowired
    private CourrierRepository courrierRepository;

    // 1. The table endpoint
    @GetMapping("/api/archived-courriers/personnel-table")
    public List<CourrierEmployeeDTO> getArchivedPersonnelCourrierTable() {
        return courrierArchiveService.getArchivedPersonnelCourrierTable();
    }

    @Autowired
    private AffectationCourrierEmployeRepository affectationCourrierEmployeRepository;

    @GetMapping("/api/courriers/{id}/download")
    public ResponseEntity<org.springframework.core.io.Resource> downloadAttachment(@PathVariable int id) {
        // Check if this courrier is linked to an employee (personnel)
        boolean isPersonnel = affectationCourrierEmployeRepository.existsByCourrier_Id(id);
        if (!isPersonnel) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to download this file.");
        }
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
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to download file: " + e.getMessage());
        }
    }



    // 3. (Optional) See courrier details

    @GetMapping("/api/courriers/{id}/view-pdf")
    public ResponseEntity<org.springframework.core.io.Resource> viewPdfInline(@PathVariable int id) {
        // Only allow if courrier is linked to an employee
        boolean isPersonnel = affectationCourrierEmployeRepository.existsByCourrier_Id(id);
        if (!isPersonnel) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to view this file.");
        }
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
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "inline; filename=\"" + file.getFileName().toString() + "\"")
                    .body(resource);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to view file: " + e.getMessage());
        }
    }

    @GetMapping("/consulter-courrier/employe")
    public List<ConsulterCourrierEmployeDTO> getCourrierEmploye() {
        List<Courrier> courriers = courrierRepository.findByType(TypeCourrier.EMPLOYE);

        return courriers.stream()
                .map(c -> new ConsulterCourrierEmployeDTO(
                        c.getDateRegistre(),
                        c.getObject(),
                        c.getEmploye().getNom(),
                        c.getEmploye().getCin()
                ))
                .collect(Collectors.toList());
    }



}
