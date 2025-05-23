package com.courrier.Bcourrier.Services;

import com.courrier.Bcourrier.DTO.RH.EmployeeListDTO;
import com.courrier.Bcourrier.Entities.AffectationService;
import com.courrier.Bcourrier.Entities.Employe;
import com.courrier.Bcourrier.Repositories.AffectationServiceRepository;
import com.courrier.Bcourrier.Repositories.EmployeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RHService {
    @Autowired
    private EmployeRepository employeRepository;
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
}
