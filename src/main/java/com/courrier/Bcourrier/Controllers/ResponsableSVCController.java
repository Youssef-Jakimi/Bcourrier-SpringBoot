package com.courrier.Bcourrier.Controllers;

import com.courrier.Bcourrier.DTO.Profile.ChangePasswordDTO;
import com.courrier.Bcourrier.DTO.Profile.PersonalInfoDTO;
import com.courrier.Bcourrier.DTO.Profile.PreferencesDTO;
import com.courrier.Bcourrier.DTO.ResposableSVC.CourrierSimpleDTO;
import com.courrier.Bcourrier.DTO.ResposableSVC.CourrierStatusUpdateDTO;
import com.courrier.Bcourrier.DTO.ResposableSVC.DashboardSVCDTO;
import com.courrier.Bcourrier.Services.ProfilService;
import com.courrier.Bcourrier.Services.ResponsableSVCService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
@Data
@RestController
@RequestMapping("/api/responsable-svc")
@RequiredArgsConstructor
public class ResponsableSVCController {
    @Autowired
    private ResponsableSVCService responsableSVCService;
    private ProfilService profilService;

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


}
