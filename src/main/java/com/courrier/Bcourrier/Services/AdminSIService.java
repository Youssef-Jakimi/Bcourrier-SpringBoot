package com.courrier.Bcourrier.Services;


import com.courrier.Bcourrier.DTO.AdminSI.AdminSICreateServiceDTO;
import com.courrier.Bcourrier.DTO.AdminSI.AdminSIDashboardDTO;
import com.courrier.Bcourrier.DTO.AdminSI.AdminSIModifyUserDTO;
import com.courrier.Bcourrier.DTO.AdminSI.AdminSIUserDTO;
import com.courrier.Bcourrier.Entities.Confidentialité;
import com.courrier.Bcourrier.Entities.Employe;
import com.courrier.Bcourrier.Entities.ServiceIntern;
import com.courrier.Bcourrier.Entities.Urgence;
import com.courrier.Bcourrier.Enums.Role;
import com.courrier.Bcourrier.Repositories.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminSIService {

    @Autowired
    private final EmployeRepository employeRepository;

    @Autowired
    private final  UrgenceRepository urgenceRepository;

    @Autowired
    private final RoleRepository roleRepository;

    @Autowired
    private final ConfidentialitéRepository confidentialiteRepository;
    @Autowired
    private final ServiceInternRepository serviceInternRepository;


    public AdminSIDashboardDTO getDashboardData() {
        AdminSIDashboardDTO dto = new AdminSIDashboardDTO();

        // Total employees
        dto.setTotalEmployees(employeRepository.count());

        // Employees needing activation (either .isActive == false OR .isCheckEmail == false, as per your business rule)
        dto.setToActivateEmployees(employeRepository.countByActiveFalse());

        // Total services
        dto.setTotalServices(serviceInternRepository.count());

        // Total roles
        dto.setTotalRoles(roleRepository.count());

        // Employees per role (for chart)
        List<Employe> allEmps = employeRepository.findAll();
        Map<String, Long> empPerRole = allEmps.stream()
                .filter(e -> e.getRole() != null)
                .collect(Collectors.groupingBy(
                        e -> e.getRole().toString(),
                        Collectors.counting()
                ));
        dto.setEmployeesPerRole(empPerRole);

        return dto;
    }

    public List<AdminSIUserDTO> getActiveUsers() {
        return employeRepository.findByActiveTrue().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<AdminSIUserDTO> getToActivateUsers() {
        // "Needs activation" logic: you can use isActive == false, or isCheckEmail == false, or both
        return employeRepository.findByActiveFalse().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private AdminSIUserDTO toDto(Employe emp) {
        AdminSIUserDTO dto = new AdminSIUserDTO();
        dto.setId((long) emp.getId());
        dto.setFullName(emp.getPrenom() + " " + emp.getNom());
        dto.setEmail(emp.getEmail());
        dto.setLogin(emp.getLogin());
        dto.setRole(emp.getRole() != null ? emp.getRole().toString() : null);
        dto.setService(emp.getService() != null ? emp.getService().getNom() : null);
        dto.setActive(emp.isActive());
        dto.setCheckEmail(emp.isCheckEmail());
        return dto;
    }

    // In AdminSIService.java

    public boolean modifyUser(AdminSIModifyUserDTO dto) {
        Optional<Employe> opt = employeRepository.findById(dto.getId());
        if (opt.isEmpty()) return false;
        Employe emp = opt.get();

        if (dto.getRole() != null) {
            emp.setRole(Role.valueOf(dto.getRole())); // Adapt if using string/enum
        }
        if (dto.getServiceId() != null) {
            serviceInternRepository.findById(dto.getServiceId())
                    .ifPresent(emp::setService);
        }
        if (dto.getActive() != null) {
            emp.setActive(dto.getActive());
        }
        employeRepository.save(emp);
        return true;
    }

    public boolean activateUser(AdminSIModifyUserDTO dto) {
        Optional<Employe> opt = employeRepository.findById(dto.getId());
        if (opt.isEmpty()) return false;
        Employe emp = opt.get();

        // Must provide BOTH role and service to activate
        if (dto.getRole() == null || dto.getServiceId() == null) return false;
        emp.setRole(Role.valueOf(dto.getRole()));
        serviceInternRepository.findById(dto.getServiceId())
                .ifPresent(emp::setService);
        emp.setActive(true);
        emp.setCheckEmail(true); // Or whatever is your logic for "activation"
        employeRepository.save(emp);
        return true;
    }


    public boolean addService(AdminSICreateServiceDTO dto) {
        if (dto.getNom() == null || dto.getNom().isBlank()) return false;
        ServiceIntern s = new ServiceIntern();
        s.setNom(dto.getNom());
        serviceInternRepository.save(s);
        return true;
    }

    // --- URGENCE ---
    public List<Urgence> getAllUrgences() {
        return urgenceRepository.findAll();
    }
    public boolean addUrgence(Urgence u) {
        if (u.getNom() == null || u.getNom().isBlank()) return false;
        urgenceRepository.save(u);
        return true;
    }

    // --- ROLE ---
    public List<com.courrier.Bcourrier.Entities.Role> getAllRoles() {
        return roleRepository.findAll();
    }
    public boolean addRole(com.courrier.Bcourrier.Entities.Role r) {
        if (r.getNom() == null || r.getNom().isBlank()) return false;
        roleRepository.save(r);
        return true;
    }

    // --- CONFIDENTIALITE ---
    public List<Confidentialité> getAllConfidentialites() {
        return confidentialiteRepository.findAll();
    }
    public boolean addConfidentialite(Confidentialité c) {
        if (c.getNom() == null || c.getNom().isBlank()) return false;
        confidentialiteRepository.save(c);
        return true;
    }

    public void deleteSVCById(Long id) {
        if (serviceInternRepository.existsById(id)) {
            serviceInternRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("ServiceIntern with ID " + id + " not found.");
        }
    }

    public void deleteURGById(Long id) {
        if (urgenceRepository.existsById(id)) {
            urgenceRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("ServiceIntern with ID " + id + " not found.");
        }
    }

    public void deleteROLEById(Long id) {
        if (roleRepository.existsById(id)) {
            roleRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("ServiceIntern with ID " + id + " not found.");
        }
    }

    public void deleteCONFById(Long id) {
        if (confidentialiteRepository.existsById(id)) {
            confidentialiteRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("ServiceIntern with ID " + id + " not found.");
        }
    }


}
