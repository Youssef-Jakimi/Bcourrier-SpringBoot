package com.courrier.Bcourrier.Services;

import com.courrier.Bcourrier.DTO.AdminBC.AdminBCDashboardDTO;
import com.courrier.Bcourrier.Entities.Courrier;
import com.courrier.Bcourrier.Entities.Depart;
import com.courrier.Bcourrier.Entities.ServiceIntern;
import com.courrier.Bcourrier.Enums.Confidentialite;
import com.courrier.Bcourrier.Enums.TypeCourrier;
import com.courrier.Bcourrier.Enums.Urgence;
import com.courrier.Bcourrier.Enums.VoieExpedition;
import com.courrier.Bcourrier.Repositories.AdminBcRepository;
import com.courrier.Bcourrier.Repositories.CourrierRepository;
import com.courrier.Bcourrier.Repositories.DepartRepository;
import com.courrier.Bcourrier.Repositories.ServiceInternRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class AdminBcService {
    @Autowired
    private final AdminBcRepository adminBcRepository;
    @Autowired
    private final DepartRepository departRepository;
    @Autowired
    private CourrierRepository courrierRepository;
    @Autowired
    private ServiceInternRepository serviceInternRepository;


    public AdminBCDashboardDTO getAdminDashboardData() {
        AdminBCDashboardDTO dto = new AdminBCDashboardDTO();

        dto.setTotalCourriersArrivee(adminBcRepository.countByTypeAndArchiverFalse("ARRIVEE"));
        dto.setTotalCourriersDepart(adminBcRepository.countByTypeAndArchiverFalse("DEPART"));
        dto.setTotalArriveeArchives(adminBcRepository.countByTypeAndArchiverTrue("ARRIVEE"));
        dto.setTotalDepartArchives(adminBcRepository.countByTypeAndArchiverTrue("DEPART"));

        // Map only safe fields for last 3 courriers
        List<Map<String, Object>> last3 = adminBcRepository.findTop3ByOrderByDateRegistreDesc()
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
                    map.put("employe", c.getEmploye() != null ? c.getEmploye().getPrenom() + " " + c.getEmploye().getNom() : null);
                    return map;
                })
                .toList();

        dto.setLast3Courriers(last3);

        // Monthly trend map
        Map<String, Long> trendMap = new LinkedHashMap<>();
        for (Object[] row : adminBcRepository.countCourriersPerMonthRaw()) {
            trendMap.put((String) row[0], (Long) row[1]);
        }
        dto.setMonthlyTrend(trendMap);

        return dto;
    }

    public void enregistrerCourrierArrivee(
            String signataire,
            String objet,
            String description,
            int numeroRegistre,
            Confidentialite degreConfidentialite,
            Urgence urgence,
            Long serviceId,
            MultipartFile file
    ) throws IOException {
        LocalDate today = LocalDate.now();
        Optional<ServiceIntern> service = serviceInternRepository.findById(serviceId);
        if (service.isEmpty()) throw new IllegalArgumentException("Service cible introuvable");

        String uploadsDir = "./uploads/courriers"; // No leading slash
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String filePath = uploadsDir + UUID.randomUUID() + "_" + originalFilename;

        Path path = Paths.get(filePath);
        Files.createDirectories(path.getParent());
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        Courrier courrier = new Courrier();
        courrier.setSignataire(signataire);
        courrier.setObject(objet);
        courrier.setDescription(description);
        courrier.setDateArrive(today);
        courrier.setDateRegistre(today);
        courrier.setNumeroRegistre(numeroRegistre);
        courrier.setDegreConfiden(degreConfidentialite);
        courrier.setUrgence(urgence);
        courrier.setAttachmentPath(filePath);
        courrier.setService(service.get());

        System.out.println("About to save: " + courrier);
        courrierRepository.save(courrier);
        System.out.println("Saved courrier");
    }


    public void enregistrerCourrierDepart(
            String objet,
            String description,
            Confidentialite degreConfidentialite,
            Urgence urgence,
            Long serviceId,
            MultipartFile attachment,
            String nomExpediteur,
            VoieExpedition voieExpedition
    ) throws IOException {

        Optional<ServiceIntern> serviceOpt = serviceInternRepository.findById(serviceId);
        if (serviceOpt.isEmpty()) {
            throw new IllegalArgumentException("Service cible introuvable");
        }

        String uploadsDir = "./uploads/courriers"; // No leading slash
        String originalFilename = StringUtils.cleanPath(attachment.getOriginalFilename());
        String filePath = uploadsDir + UUID.randomUUID() + "_" + originalFilename;
        Path path = Paths.get(filePath);
        Files.createDirectories(path.getParent());
        Files.copy(attachment.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        Courrier courrier = new Courrier();
        courrier.setObject(objet);
        courrier.setDescription(description);
        courrier.setDegreConfiden(degreConfidentialite);
        courrier.setUrgence(urgence);
        courrier.setAttachmentPath(filePath);
        courrier.setService(serviceOpt.get());
        courrier.setType(TypeCourrier.DEPARD);
        courrierRepository.save(courrier);

        Depart depart = new Depart();
        depart.setNomExpediteur(nomExpediteur);
        depart.setVoieExpedition(voieExpedition);
        depart.setCourrier(courrier);
        departRepository.save(depart);
    }


}