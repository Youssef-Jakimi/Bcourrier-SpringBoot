package com.courrier.Bcourrier.Services;

import com.courrier.Bcourrier.DTO.ResposableSVC.CourrierSimpleDTO;
import com.courrier.Bcourrier.DTO.ResposableSVC.CourrierStatusUpdateDTO;
import com.courrier.Bcourrier.DTO.ResposableSVC.DashboardSVCDTO;
import com.courrier.Bcourrier.DTO.ResposableSVC.DashboardSVCDTO;
import com.courrier.Bcourrier.Entities.Courrier;
import com.courrier.Bcourrier.Entities.Employe;
import com.courrier.Bcourrier.Entities.ServiceIntern;
import com.courrier.Bcourrier.Enums.StatutCourrier;
import com.courrier.Bcourrier.Enums.TypeCourrier;
import com.courrier.Bcourrier.Repositories.CourrierRepository;
import com.courrier.Bcourrier.Repositories.EmployeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ResponsableSVCService {
    @Autowired
    private EmployeRepository employeRepository;
    @Autowired
    private CourrierRepository courrierRepository;

    private ServiceIntern getCurrentResponsableService(String login) {
        Employe responsable = employeRepository.findByLogin(login).orElse(null);
        if (responsable == null) return null;
        return responsable.getService();
    }

    // DASHBOARD BRIEF
    public DashboardSVCDTO getDashboardBrief(String login) {
        ServiceIntern svc = getCurrentResponsableService(login);
        DashboardSVCDTO dto = new DashboardSVCDTO();
        dto.setArriveeEnCours(courrierRepository.countByServiceAndTypeAndArchiverFalse(svc, TypeCourrier.ARRIVEE));
        dto.setArriveeArchive(courrierRepository.countByServiceAndTypeAndArchiverTrue(svc, TypeCourrier.ARRIVEE));
        dto.setDepartEnCours(courrierRepository.countByServiceAndTypeAndArchiverFalse(svc, TypeCourrier.DEPART));
        dto.setDepartArchive(courrierRepository.countByServiceAndTypeAndArchiverTrue(svc, TypeCourrier.DEPART));
        return dto;
    }

    // LIST ARRIVEE EN COURS
    public List<CourrierSimpleDTO> getArriveeEnCours(String login) {
        ServiceIntern svc = getCurrentResponsableService(login);
        return courrierRepository.findByServiceAndTypeAndArchiverFalse(svc, TypeCourrier.ARRIVEE)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    // LIST ARRIVEE ARCHIVE
    public List<CourrierSimpleDTO> getArriveeArchive(String login) {
        ServiceIntern svc = getCurrentResponsableService(login);
        return courrierRepository.findByServiceAndTypeAndArchiverTrue(svc, TypeCourrier.ARRIVEE)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    // LIST DEPART EN COURS
    public List<CourrierSimpleDTO> getDepartEnCours(String login) {
        ServiceIntern svc = getCurrentResponsableService(login);
        return courrierRepository.findByServiceAndTypeAndArchiverFalse(svc, TypeCourrier.DEPART)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    // LIST DEPART ARCHIVE
    public List<CourrierSimpleDTO> getDepartArchive(String login) {
        ServiceIntern svc = getCurrentResponsableService(login);
        return courrierRepository.findByServiceAndTypeAndArchiverTrue(svc, TypeCourrier.DEPART)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    // Utility mapper
    private CourrierSimpleDTO toDto(Courrier c) {
        CourrierSimpleDTO dto = new CourrierSimpleDTO();
        dto.setId((long) c.getId());
        dto.setObjet(c.getObject());
        dto.setDescription(c.getDescription());
        dto.setType(String.valueOf(c.getType()));
        dto.setDateArrive(c.getDateArrive() != null ? c.getDateArrive().toString() : null);
        dto.setDateRegistre(c.getDateRegistre() != null ? c.getDateRegistre().toString() : null);
        dto.setArchiver(c.isArchiver());
        dto.setSignataire(c.getSignataire());
        dto.setUrgence(c.getUrgence() != null ? c.getUrgence().toString() : null);
        return dto;
    }

    public boolean updateCourrierStatus(String login, CourrierStatusUpdateDTO dto) {
        Employe responsable = employeRepository.findByLogin(login).orElse(null);
        if (responsable == null || responsable.getService() == null) return false;

        Courrier courrier = courrierRepository.findById(dto.getCourrierId()).orElse(null);
        if (courrier == null) return false;

        // Only allow update if this courrier belongs to the responsible's service
        if (courrier.getService() == null ||
                !courrier.getService().getId().equals(responsable.getService().getId())) {
            return false;
        }

        // Here you assume you have a 'statut' or similar field in Courrier (String or Enum)
        courrier.setStatutCourrier(StatutCourrier.valueOf(dto.getNewStatus()));
        courrierRepository.save(courrier);
        return true;
    }
}
