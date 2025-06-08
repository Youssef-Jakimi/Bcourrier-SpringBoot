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

import java.time.LocalDate;
import java.util.ArrayList;
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

    public DashboardSVCDTO getDashboardBrief(String login) {
        ServiceIntern svc = getCurrentResponsableService(login);
        DashboardSVCDTO dto = new DashboardSVCDTO();

        // Basic counts
        dto.setArriveeEnCours(courrierRepository.countByServiceAndTypeAndArchiverFalse(svc, TypeCourrier.ARRIVEE));
        dto.setArriveeArchive(courrierRepository.countByServiceAndTypeAndArchiverTrue(svc, TypeCourrier.ARRIVEE));
        dto.setDepartEnCours(courrierRepository.countByServiceAndTypeAndArchiverFalse(svc, TypeCourrier.DEPART));
        dto.setDepartArchive(courrierRepository.countByServiceAndTypeAndArchiverTrue(svc, TypeCourrier.DEPART));

        // Graph data
        List<Courrier> courriers = courrierRepository.findByService(svc);

        List<String> monthLabels = new ArrayList<>();
        List<Integer> monthlyArrivees = new ArrayList<>();
        List<Integer> monthlyDeparts = new ArrayList<>();

        LocalDate now = LocalDate.now();

        for (int i = 0; i < 12; i++) {
            LocalDate month = now.minusMonths(11 - i);
            String label = month.getMonth().toString().substring(0, 1).toUpperCase() +
                    month.getMonth().toString().substring(1, 3).toLowerCase();
            monthLabels.add(label);

            int countArr = (int) courriers.stream()
                    .filter(c ->
                            c.getType() == TypeCourrier.ARRIVEE &&
                                    c.getDateRegistre() != null &&
                                    c.getDateRegistre().getMonthValue() == month.getMonthValue() &&
                                    c.getDateRegistre().getYear() == month.getYear()
                    ).count();

            int countDep = (int) courriers.stream()
                    .filter(c ->
                            c.getType() == TypeCourrier.DEPART &&
                                    c.getDateRegistre() != null &&
                                    c.getDateRegistre().getMonthValue() == month.getMonthValue() &&
                                    c.getDateRegistre().getYear() == month.getYear()
                    ).count();

            monthlyArrivees.add(countArr);
            monthlyDeparts.add(countDep);
        }

        dto.setMonthlyLabels(monthLabels);
        dto.setMonthlyArrivees(monthlyArrivees);
        dto.setMonthlyDeparts(monthlyDeparts);

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

        // Update status
        StatutCourrier newStatut = StatutCourrier.valueOf(dto.getNewStatus());
        courrier.setStatutCourrier(newStatut);

         if (newStatut == StatutCourrier.TRAITE) {
            courrier.setArchiver(true);
        }
        // If status is RETOUR, set service and employe to null
        if (newStatut == StatutCourrier.RETOURE) {
            courrier.setService(null);
            courrier.setEmploye(null);
        }

        courrierRepository.save(courrier);
        return true;
    }

}
