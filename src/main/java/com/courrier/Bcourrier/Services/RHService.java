package com.courrier.Bcourrier.Services;

import com.courrier.Bcourrier.DTO.RH.CourrierEmployeeDTO;
import com.courrier.Bcourrier.DTO.RH.EmployeeListDTO;
import com.courrier.Bcourrier.DTO.RH.RhDashboardDTO;
import com.courrier.Bcourrier.Entities.AffectationCourrierEmploye;
import com.courrier.Bcourrier.Entities.AffectationService;
import com.courrier.Bcourrier.Entities.Employe;
import com.courrier.Bcourrier.Repositories.AffectationCourrierEmployeRepository;
import com.courrier.Bcourrier.Repositories.AffectationServiceRepository;
import com.courrier.Bcourrier.Repositories.CourrierRepository;
import com.courrier.Bcourrier.Repositories.EmployeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RHService {


    private final CourrierRepository courrierRepository;
    private final EmployeRepository employeRepository;

    public RhDashboardDTO getDashboardData() {
        RhDashboardDTO dto = new RhDashboardDTO();

        dto.setTotalEmployes(employeRepository.count());
        dto.setTotalCourriersArchives(courrierRepository.countByArchiverTrue());
        dto.setTotalCourriersTraites(courrierRepository.countByDateTraitementNotNull());
        dto.setTotalNonTraites(courrierRepository.countByDateTraitementNull());

        List<Map<String, Object>> last3 = courrierRepository.findTop3ByArchiverTrueOrderByDateRegistreDesc()
                .stream()
                .map(c -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", c.getId());
                    map.put("objet", c.getObject());
                    map.put("type", c.getType());
                    map.put("dateRegistre", c.getDateRegistre());
                    map.put("service", c.getService() != null ? c.getService().getNom() : null);
                    map.put("employe", c.getEmploye() != null ? c.getEmploye().getPrenom() + " " + c.getEmploye().getNom() : null);
                    return map;
                }).toList();

        dto.setLast3Archives(last3);

        Map<String, Long> trend = new LinkedHashMap<>();
        for (Object[] row : courrierRepository.countArchivedCourriersPerMonthRaw()) {
            trend.put((String) row[0], (Long) row[1]);
        }
        dto.setMonthlyTrend(trend);

        return dto;
    }


    @Autowired
    private AffectationServiceRepository affectationServiceRepository;

    public List<EmployeeListDTO> getAllEmployeeList() {
        List<Employe> employees = employeRepository.findAll();
        return employees.stream().map(emp -> {
            EmployeeListDTO dto = new EmployeeListDTO();
            dto.setFullName(emp.getPrenom() + " " + emp.getNom());
            dto.setEmail(emp.getEmail());
            dto.setLogin(emp.getLogin());
            dto.setRole(emp.getRole() != null ? emp.getRole().toString() : null);
            dto.setService(emp.getService() != null ? emp.getService().getNom() : null);
            dto.setActive(emp.isActive());
            // Fetch earliest affectation date for this employee
            Optional<AffectationService> earliestAffectation = affectationServiceRepository
                    .findTopByEmployeOrderByDateAffectationAsc(emp);
            dto.setEmployeeSince(earliestAffectation
                    .map(a -> a.getDateAffectation().toString())
                    .orElse(null));
            return dto;
        }).collect(Collectors.toList());
    }


    @Autowired
    private AffectationCourrierEmployeRepository affectationCourrierEmployeRepository;

    public List<CourrierEmployeeDTO> getArchivedPersonnelCourrierTable() {
        List<AffectationCourrierEmploye> affectations = affectationCourrierEmployeRepository.findByCourrier_ArchiverTrue();
        return affectations.stream().map(a -> {
            CourrierEmployeeDTO dto = new CourrierEmployeeDTO();
            dto.setCourrierId((long) a.getCourrier().getId());
            dto.setMatricule(a.getEmploye().getMatricule());
            dto.setCin(a.getEmploye().getCin());
            dto.setObjet(a.getCourrier().getObject());
            dto.setEmploye(a.getEmploye().getPrenom() + " " + a.getEmploye().getNom());
            dto.setService(a.getEmploye().getService() != null ? a.getEmploye().getService().getNom() : null);
            dto.setDateArchivage(
                    a.getCourrier().getDateRegistre() != null
                            ? a.getCourrier().getDateRegistre().toString()
                            : (a.getCourrier().getDateRegistre() != null ? a.getCourrier().getDateRegistre().toString() : null)
            );
            dto.setStatut(a.getCourrier().getStatutCourrier() != null ? a.getCourrier().getStatutCourrier().toString() : "Traité");
            dto.setAttachmentName(a.getCourrier().getAttachmentPath() != null
                    ? a.getCourrier().getAttachmentPath().substring(a.getCourrier().getAttachmentPath().lastIndexOf('/') + 1)
                    : null);
            dto.setDownloadUrl("/api/courriers/" + a.getCourrier().getId() + "/download");
            return dto;
        }).collect(Collectors.toList());
    }
}
