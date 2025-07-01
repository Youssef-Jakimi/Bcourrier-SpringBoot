package com.courrier.Bcourrier.Services;

import com.courrier.Bcourrier.DTO.ResposableSVC.CourrierSimpleDTO;
import com.courrier.Bcourrier.DTO.ResposableSVC.CourrierStatusUpdateDTO;
import com.courrier.Bcourrier.DTO.ResposableSVC.DashboardSVCDTO;
import com.courrier.Bcourrier.DTO.ResposableSVC.DashboardSVCDTO;
import com.courrier.Bcourrier.Entities.AffectationCourrierService;
import com.courrier.Bcourrier.Entities.Courrier;
import com.courrier.Bcourrier.Entities.Employe;
import com.courrier.Bcourrier.Entities.ServiceIntern;
import com.courrier.Bcourrier.Enums.StatutCourrier;
import com.courrier.Bcourrier.Enums.TypeCourrier;
import com.courrier.Bcourrier.Repositories.AffectationCourrierServiceRepository;
import com.courrier.Bcourrier.Repositories.CourrierRepository;
import com.courrier.Bcourrier.Repositories.EmployeRepository;
import com.courrier.Bcourrier.Repositories.ResponsableSVCRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.time.*;
import java.util.stream.Collectors;

import static java.time.LocalTime.now;

@Service
@RequiredArgsConstructor
public class ResponsableSVCService {
    @Autowired
    private final EmployeRepository employeRepository;
    private final ResponsableSVCRepository responsableSVCRepository;
    private final AffectationCourrierServiceRepository affectationCourrierServiceRepository;
    @Autowired
    private final CourrierRepository courrierRepository;

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

        // ─── Last 3 courriers ───
        List<Map<String, Object>> last3 = responsableSVCRepository.findTop3ByServiceOrderByDateRegistreDesc(svc)
                .stream()
                .map(c -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", c.getId());
                    map.put("objet", c.getObject());
                    map.put("description", c.getDescription());
                    map.put("type", c.getType());
                    map.put("dateRegistre", c.getDateRegistre());
                    map.put("signataire", c.getSignataire());
                    map.put("service", c.getService() != null ? c.getService().getNom() : null);
                    map.put(
                            "employe",
                            c.getEmploye() != null
                                    ? c.getEmploye().getPrenom() + " " + c.getEmploye().getNom()
                                    : null
                    );
                    return map;
                })
                .toList();
        dto.setLast3Courriers(last3);

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

        return courrierRepository
                .findByServiceAndTypeAndArchiverFalse(svc, TypeCourrier.ARRIVEE)
                .stream()
                .map(courrier -> {
                    CourrierSimpleDTO dto = new CourrierSimpleDTO();
                    dto.setId(courrier.getId());
                    dto.setObjet(courrier.getObject());
                    dto.setDescription(courrier.getDescription());
                    dto.setType(courrier.getType().name());
                    dto.setDateArrive(courrier.getDateArrive() != null ? courrier.getDateArrive().toString() : null);
                    dto.setDateRegistre(courrier.getDateRegistre() != null ? courrier.getDateRegistre().toString() : null);
                    dto.setArchiver(courrier.isArchiver());
                    dto.setSignataire(courrier.getSignataire());
                    dto.setStatus(courrier.getStatutCourrier().toString());
                    dto.setUrgence(courrier.getUrgence().getNom());

                    // Set the list of possible statuses
                    dto.setStatusCourrierList(Arrays.asList(StatutCourrier.values()));

                    return dto;
                })
                .collect(Collectors.toList());
    }


    // LIST ARRIVEE ARCHIVE
    public List<CourrierSimpleDTO> getArriveeArchive(String login) {
        ServiceIntern svc = getCurrentResponsableService(login);
        return courrierRepository
                .findByServiceAndTypeAndArchiverTrue(svc, TypeCourrier.ARRIVEE)
                .stream()
                .map(courrier -> {
                    CourrierSimpleDTO dto = new CourrierSimpleDTO();
                    dto.setId(courrier.getId());
                    dto.setObjet(courrier.getObject());
                    dto.setDescription(courrier.getDescription());
                    dto.setType(courrier.getType().name());
                    dto.setDateArrive(courrier.getDateArrive() != null ? courrier.getDateArrive().toString() : null);
                    dto.setDateRegistre(courrier.getDateRegistre() != null ? courrier.getDateRegistre().toString() : null);
                    dto.setArchiver(courrier.isArchiver());
                    dto.setStatus(courrier.getStatutCourrier().toString());
                    dto.setSignataire(courrier.getSignataire());

                    // Set the list of possible statuses
                    dto.setStatusCourrierList(Arrays.asList(StatutCourrier.values()));

                    return dto;
                })
                .collect(Collectors.toList());
    }

    // LIST DEPART EN COURS
    public List<CourrierSimpleDTO> getDepartEnCours(String login) {
        ServiceIntern svc = getCurrentResponsableService(login);
        return courrierRepository.findByServiceAndTypeAndArchiverFalse(svc, TypeCourrier.DEPART)
                .stream()
                .map(courrier -> {
                    CourrierSimpleDTO dto = new CourrierSimpleDTO();
                    dto.setId(courrier.getId());
                    dto.setObjet(courrier.getObject());
                    dto.setDescription(courrier.getDescription());
                    dto.setType(courrier.getType().name());
                    dto.setDateArrive(courrier.getDateArrive() != null ? courrier.getDateArrive().toString() : null);
                    dto.setDateRegistre(courrier.getDateRegistre() != null ? courrier.getDateRegistre().toString() : null);
                    dto.setArchiver(courrier.isArchiver());
                    dto.setSignataire(courrier.getSignataire());
                    dto.setStatus(courrier.getStatutCourrier().toString());
                    dto.setUrgence(courrier.getUrgence().getNom());

                    // Set the list of possible statuses
                    dto.setStatusCourrierList(Arrays.asList(StatutCourrier.values()));

                    return dto;
                })
                .collect(Collectors.toList());
    }
    // LIST DEPART ARCHIVE
    public List<CourrierSimpleDTO> getDepartArchive(String login) {
        ServiceIntern svc = getCurrentResponsableService(login);
        return courrierRepository.findByServiceAndTypeAndArchiverTrue(svc, TypeCourrier.DEPART)
                .stream()
                .map(courrier -> {
                    CourrierSimpleDTO dto = new CourrierSimpleDTO();
                    dto.setId(courrier.getId());
                    dto.setObjet(courrier.getObject());
                    dto.setDescription(courrier.getDescription());
                    dto.setType(courrier.getType().name());
                    dto.setDateArrive(courrier.getDateArrive() != null ? courrier.getDateArrive().toString() : null);
                    dto.setDateRegistre(courrier.getDateRegistre() != null ? courrier.getDateRegistre().toString() : null);
                    dto.setArchiver(courrier.isArchiver());
                    dto.setStatus(courrier.getStatutCourrier().toString());
                    dto.setSignataire(courrier.getSignataire());

                    // Set the list of possible statuses
                    dto.setStatusCourrierList(Arrays.asList(StatutCourrier.values()));

                    return dto;
                })
                .collect(Collectors.toList());
    }

    // Utility mapper
    private CourrierSimpleDTO toDto(Courrier c) {
        CourrierSimpleDTO dto = new CourrierSimpleDTO();
        dto.setId(c.getId());
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
        AffectationCourrierService courriersvc = affectationCourrierServiceRepository.findByCourrier_id(dto.getCourrierId());
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
             courriersvc.setDateFinExecution(LocalDate.now());
             courrier.setDateTraitement(LocalDate.now());
        }
        if (newStatut == StatutCourrier.EN_COURS) {
            courriersvc.setDateConsultation(LocalDate.now());
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
