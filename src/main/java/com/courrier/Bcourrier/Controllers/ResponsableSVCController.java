package com.courrier.Bcourrier.Controllers;

import com.courrier.Bcourrier.DTO.ResposableSVC.CourrierSimpleDTO;
import com.courrier.Bcourrier.DTO.ResposableSVC.DashboardSVCDTO;
import com.courrier.Bcourrier.Services.ResponsableSVCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/responsable-svc")
public class ResponsableSVCController {
    @Autowired
    private ResponsableSVCService responsableSVCService;

    // DASHBOARD BRIEF
    @GetMapping("/dashboard")
    public DashboardSVCDTO getDashboardBrief(@AuthenticationPrincipal UserDetails userDetails) {
        String login = userDetails.getUsername();
        return responsableSVCService.getDashboardBrief(login);
    }

    // ARRIVEE EN COURS
    @GetMapping("/courriers/arrivee/encours")
    public List<CourrierSimpleDTO> getArriveeEnCours(@AuthenticationPrincipal UserDetails userDetails) {
        String login = userDetails.getUsername();
        return responsableSVCService.getArriveeEnCours(login);
    }

    // ARRIVEE ARCHIVES
    @GetMapping("/courriers/arrivee/archives")
    public List<CourrierSimpleDTO> getArriveeArchive(@AuthenticationPrincipal UserDetails userDetails) {
        String login = userDetails.getUsername();
        return responsableSVCService.getArriveeArchive(login);
    }

    // DEPART EN COURS
    @GetMapping("/courriers/depart/encours")
    public List<CourrierSimpleDTO> getDepartEnCours(@AuthenticationPrincipal UserDetails userDetails) {
        String login = userDetails.getUsername();
        return responsableSVCService.getDepartEnCours(login);
    }

    // DEPART ARCHIVES
    @GetMapping("/courriers/depart/archives")
    public List<CourrierSimpleDTO> getDepartArchive(@AuthenticationPrincipal UserDetails userDetails) {
        String login = userDetails.getUsername();
        return responsableSVCService.getDepartArchive(login);
    }
}
