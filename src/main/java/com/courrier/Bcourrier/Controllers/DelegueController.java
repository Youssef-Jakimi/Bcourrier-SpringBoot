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
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
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


    @GetMapping("/courriers/departs")
    public ResponseEntity<List<CourrierDepartDTO>> getDepartCourriers() {
        return ResponseEntity.ok(delegueService.getAllDepartCourrierDTOs());
    }


    @GetMapping("/stats")
    public ResponseEntity<StatsDTO> getStats() {
        return ResponseEntity.ok(delegueService.getStats());
    }

    @GetMapping("/support")
    public ResponseEntity<InputStreamResource> viewPdf() {
        try {
            ClassPathResource pdfFile = new ClassPathResource("support/DELEGUE.pdf");

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
