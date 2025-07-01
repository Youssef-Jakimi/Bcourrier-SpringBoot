package com.courrier.Bcourrier.Services;

import com.courrier.Bcourrier.DTO.AdminBC.*;
import com.courrier.Bcourrier.Entities.*;
import com.courrier.Bcourrier.Enums.StatutCourrier;
import com.courrier.Bcourrier.Enums.TypeCourrier;
import com.courrier.Bcourrier.Repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class AdminBcService {
    @Autowired
    private final AdminBcRepository adminBcRepository;
    private final EmployeRepository employeRepository;
    private final UrgenceRepository urgenceRepository;
    private final AffectationCourrierServiceRepository affectationCourrierServiceRepository;
    private final AffectationCourrierEmployeRepository affectationCourrierEmployeRepository;
    private final ConfidentialiteRepository confidentialiteRepository;
    @Autowired
    private final DepartRepository departRepository;
    @Autowired
    private CourrierRepository courrierRepository;
    @Autowired
    private ServiceInternRepository serviceInternRepository;


    public AdminBCDashboardDTO getAdminDashboardData() {
        AdminBCDashboardDTO dto = new AdminBCDashboardDTO();

        // ─── Totals ───
        dto.setTotalCourriersArrivee(
                adminBcRepository.countByTypeAndArchiverFalse(TypeCourrier.ARRIVEE)
        );
        dto.setTotalCourriersDepart(
                adminBcRepository.countByTypeAndArchiverFalse(TypeCourrier.DEPART)
        );
        dto.setTotalArriveeArchives(
                adminBcRepository.countByTypeAndArchiverTrue(TypeCourrier.ARRIVEE)
        );
        dto.setTotalDepartArchives(
                adminBcRepository.countByTypeAndArchiverTrue(TypeCourrier.DEPART)
        );

        // ─── Last 3 courriers ───
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

        // ─── Monthly trend: last 7 months (ARRIVEE vs DEPART) ───
        List<Courrier> courriers = courrierRepository.findAll();

        List<String> monthLabels   = new ArrayList<>();
        List<Integer> monthlyArrivees = new ArrayList<>();
        List<Integer> monthlyDeparts  = new ArrayList<>();

        LocalDate now = LocalDate.now(); // system default zone (Africa/Casablanca in your setup)

        // Loop from (now.minusMonths(6)) up to current month: total of 7 months.
        for (int i = 0; i < 12; i++) {
            LocalDate month = now.minusMonths(11 - i);
            // Label = first three letters with initial uppercase, e.g. "Jan", "Feb"
            String label = month.getMonth().toString().substring(0, 1).toUpperCase() +
                    month.getMonth().toString().substring(1, 3).toLowerCase();
            monthLabels.add(label);

            int countArr = (int) courriers.stream()
                    .filter(c ->
                            c.getType() == TypeCourrier.ARRIVEE
                                    && c.getDateRegistre() != null
                                    && c.getDateRegistre().getMonthValue() == month.getMonthValue()
                                    && c.getDateRegistre().getYear() == month.getYear()
                    )
                    .count();

            int countDep = (int) courriers.stream()
                    .filter(c ->
                            c.getType() == TypeCourrier.DEPART
                                    && c.getDateRegistre() != null
                                    && c.getDateRegistre().getMonthValue() == month.getMonthValue()
                                    && c.getDateRegistre().getYear() == month.getYear()
                    )
                    .count();

            monthlyArrivees.add(countArr);
            monthlyDeparts.add(countDep);
        }

        dto.setMonthlyLabels(monthLabels);
        dto.setMonthlyArrivees(monthlyArrivees);
        dto.setMonthlyDeparts(monthlyDeparts);

        return dto;
    }


    public void enregistrerCourrierArrivee(
            String signataire,
            String nature,
            String objet,
            String description,
            int numeroRegistre,
            Confidentialite degreConfidentialite,
            Urgence urgence,
            LocalDate dateArrive,
            LocalDate dateEnregistre,
            Integer reponseAId,
            Long serviceId,
            Integer employe,
            MultipartFile file
    ) throws IOException {
        LocalDate today = LocalDate.now();
        Optional<ServiceIntern> service = serviceInternRepository.findById(serviceId);
        if (service.isEmpty()) throw new IllegalArgumentException("Service cible introuvable");
        Courrier reponseCourrier = null;
        if (reponseAId != null) {
            reponseCourrier = courrierRepository.findById(reponseAId)
                    .orElseThrow(() -> new IllegalArgumentException("Courrier source introuvable"));
        }
        Employe employesible = null;
        if (employe != null) {
            employesible = employeRepository.findById(Long.valueOf(employe))
                    .orElseThrow(() -> new IllegalArgumentException("employe introuvable"));
        }
        String uploadsDir = "./uploads/courriers"; // No leading slash
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String filePath = uploadsDir + UUID.randomUUID() + "_" + originalFilename;

        Path path = Paths.get(filePath);
        Files.createDirectories(path.getParent());
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        Courrier courrier = new Courrier();
        courrier.setSignataire(signataire);
        courrier.setNature(nature);
        courrier.setObject(objet);
        courrier.setDescription(description);
        courrier.setDateArrive(today);
        courrier.setDateRegistre(today);
        courrier.setNumeroRegistre(numeroRegistre);
        courrier.setDegreConfiden(degreConfidentialite);
        courrier.setUrgence(urgence);
        courrier.setType(TypeCourrier.ARRIVEE);
        courrier.setAttachmentPath(filePath);
        courrier.setService(service.get());
        courrier.setStatutCourrier(StatutCourrier.ENREGISTRE);
        courrier.setDateArrive(dateArrive);
        courrier.setDateRegistre(dateEnregistre);
        if (reponseCourrier != null) {
            courrier.setReponseA(reponseCourrier);
        }
        if (employesible != null) {
            courrier.setEmploye(employesible);
            AffectationCourrierEmploye courrierEmploye = new AffectationCourrierEmploye();
            courrierEmploye.setCourrier(courrier);
            courrierEmploye.setEmploye(employesible);
            courrierEmploye.setDateAffection(today);
            courrierEmploye.setHeureAffectation(LocalTime.now().toString());
            affectationCourrierEmployeRepository.save(courrierEmploye);
        }
        courrierRepository.save(courrier);

        // 👇 Now create the affectation entry
        AffectationCourrierService affectation = new AffectationCourrierService();
        affectation.setCourrier(courrier);
        affectation.setService(serviceInternRepository.findById(serviceId).orElseThrow());
        affectation.setDateAffection(today);
        affectation.setHeureAffectation(LocalTime.now().toString());
        affectation.setTypeAffectation("Automatique");

        affectationCourrierServiceRepository.save(affectation);
        System.out.println("Saved courrier");
    }


    public void enregistrerCourrierDepart(
            String objet,
            String nature,
            String description,
            Confidentialite degreConfidentialite,
            Urgence urgence,
            int numeroRegistre,
            Long serviceId,
            MultipartFile attachment,
            String nomExpediteur,
            VoieExpedition voieExpedition,
            LocalDate dateDepart,
            LocalDate dateRegistre,
            Integer reponseAId // 👈 New parameter


    ) throws IOException {
        Optional<ServiceIntern> serviceOpt = serviceInternRepository.findById(serviceId);
        if (serviceOpt.isEmpty()) {
            throw new IllegalArgumentException("Service cible introuvable");
        }
        Courrier reponseCourrier = null;
        if (reponseAId != null) {
            reponseCourrier = courrierRepository.findById(reponseAId)
                    .orElseThrow(() -> new IllegalArgumentException("Courrier source introuvable"));
        }
        String uploadsDir = "./uploads/courriers";
        String originalFilename = StringUtils.cleanPath(attachment.getOriginalFilename());
        String filePath = uploadsDir + UUID.randomUUID() + "_" + originalFilename;
        Path path = Paths.get(filePath);
        Files.createDirectories(path.getParent());
        Files.copy(attachment.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        Courrier courrier = new Courrier();
        courrier.setObject(objet);
        courrier.setNature(nature);
        courrier.setDescription(description);
        courrier.setDegreConfiden(degreConfidentialite);
        courrier.setUrgence(urgence);
        courrier.setNumeroRegistre(numeroRegistre);
        courrier.setAttachmentPath(filePath);
        courrier.setService(serviceOpt.get());
        courrier.setType(TypeCourrier.DEPART);

        // 👇 Link response if available
        if (reponseCourrier != null) {
            courrier.setReponseA(reponseCourrier);
        }

        // ✅ Use provided dates
        courrier.setDateRegistre(dateRegistre);

        courrierRepository.save(courrier);

        Depart depart = new Depart();
        depart.setNomExpediteur(nomExpediteur);
        depart.setVoieExpedition(voieExpedition);
        depart.setDateExpedition(dateDepart);
        depart.setCourrier(courrier);
        departRepository.save(depart);

        AffectationCourrierService affectation = new AffectationCourrierService();
        affectation.setCourrier(courrier);
        affectation.setService(serviceOpt.get());
        affectation.setDateAffection(LocalDate.now());
        affectation.setHeureAffectation(LocalTime.now().toString());
        affectation.setTypeAffectation("Automatique");

        affectationCourrierServiceRepository.save(affectation);
    }




    public CourrierArriveeResponseDTO getAllArriveeCourrierDTOs() {
        CourrierArriveeResponseDTO resp = new CourrierArriveeResponseDTO();

        List<CourrierArriveeDTO> courriers = courrierRepository.findByType(TypeCourrier.ARRIVEE)
                .stream()
                .map(c -> {
                    CourrierArriveeDTO dto = new CourrierArriveeDTO();
                    dto.setId(c.getId());
                    dto.setObject(c.getObject());
                    dto.setDescription(c.getDescription());
                    dto.setDateArrive(c.getDateArrive() != null ? c.getDateArrive().toString() : null);
                    dto.setDateTraitement(c.getDateTraitement() != null ? c.getDateTraitement().toString() : null);
                    dto.setStatutCourrier(c.getStatutCourrier() != null ? c.getStatutCourrier().toString() : null);
                    dto.setNumeroRegistre(c.getNumeroRegistre());
                    dto.setDateRegistre(c.getDateRegistre() != null ? c.getDateRegistre().toString() : null);
                    dto.setSignataire(c.getSignataire());
                    dto.setArchiver(c.isArchiver());
                    dto.setService(c.getService() != null ? c.getService().getNom() : null);
                    dto.setEmploye(c.getEmploye() != null ? c.getEmploye().getPrenom() + " " + c.getEmploye().getNom() : null);
                    dto.setNature(c.getNature());
                    dto.setDegreConfiden(c.getDegreConfiden() != null ? c.getDegreConfiden().getNom() : null);
                    dto.setUrgence(c.getUrgence() != null ? c.getUrgence().getNom() : null);
                    dto.setMotDes(c.getMotDes());
                    return dto;
                })
                .toList();

        resp.setCourriers(courriers);
        resp.setServices(serviceInternRepository.findAll());
        resp.setUrgences(urgenceRepository.findAll());
        resp.setConfidentialites(confidentialiteRepository.findAll());
        return resp;
    }

    public CourrierDepartResponseDTO getAllDepartCourrierDTOs() {
        CourrierDepartResponseDTO resp = new CourrierDepartResponseDTO();

        List<CourrierDepartDTO> courriers = departRepository.findAll()
                .stream()
                .map(d -> {
                    CourrierDepartDTO dto = new CourrierDepartDTO();
                    dto.setId(d.getId());
                    dto.setObject(d.getCourrier().getObject());
                    dto.setDescription(d.getCourrier().getDescription());
                    dto.setDateDepart(d.getCourrier().getDateRegistre() != null ? d.getCourrier().getDateRegistre().toString() : null);
                    dto.setNumeroRegistre(d.getCourrier().getNumeroRegistre());
                    dto.setArchiver(d.getCourrier().isArchiver());
                    dto.setStatutCourrier(d.getCourrier().getStatutCourrier() != null ? d.getCourrier().getStatutCourrier().toString() : null);
                    dto.setService(d.getCourrier().getService() != null ? d.getCourrier().getService().getNom() : null);
                    dto.setEmploye(d.getCourrier().getEmploye() != null ? d.getCourrier().getEmploye().getPrenom() + " " + d.getCourrier().getEmploye().getNom() : null);
                    dto.setNomExpediteur(d.getNomExpediteur());
                    dto.setVoieExpedition(d.getVoieExpedition() != null ? d.getVoieExpedition().getNom() : null);
                    dto.setNature(d.getCourrier().getNature());
                    dto.setDegreConfiden(d.getCourrier().getDegreConfiden() != null ? d.getCourrier().getDegreConfiden().getNom() : null);
                    dto.setUrgence(d.getCourrier().getUrgence() != null ? d.getCourrier().getUrgence().getNom() : null);
                    dto.setMotDes(d.getCourrier().getMotDes());
                    return dto;
                })
                .toList();

        resp.setCourriers(courriers);
        resp.setServices(serviceInternRepository.findAll());
        resp.setUrgences(urgenceRepository.findAll());
        resp.setConfidentialites(confidentialiteRepository.findAll());
        return resp;
    }



    public StatsDTO getStats() {
        StatsDTO dto = new StatsDTO();

        // 1. Total employees
        dto.setTotalEmployes((int) employeRepository.count());

        // 2. Total active services
        dto.setTotalServices((int) serviceInternRepository.count());

        // 3. Courriers urgents
        int urgentCount = (int) courrierRepository.findAll().stream()
                .filter(c -> c.getUrgence() == urgenceRepository.findByNom("URGENT"))
                .count();
        dto.setTotalUrgentCourriers(urgentCount);

        // 4. Monthly trend: last 12 months, by type
        List<Courrier> courriers = courrierRepository.findAll();
        List<String> monthLabels = new ArrayList<>();
        List<Integer> monthlyArrivees = new ArrayList<>();
        List<Integer> monthlyDeparts = new ArrayList<>();
        LocalDate now = LocalDate.now();
        for (int i = 0; i < 7; i++) { // for latest 7 months (adjust for more/less)
            LocalDate month = now.minusMonths(6 - i);
            String label = month.getMonth().toString().substring(0, 1).toUpperCase() +
                    month.getMonth().toString().substring(1, 3).toLowerCase();
            monthLabels.add(label);

            int countArr = (int) courriers.stream()
                    .filter(c -> "ARRIVEE".equalsIgnoreCase(c.getType().toString())
                            && c.getDateRegistre() != null
                            && c.getDateRegistre().getMonthValue() == month.getMonthValue()
                            && c.getDateRegistre().getYear() == month.getYear())
                    .count();
            int countDep = (int) courriers.stream()
                    .filter(c -> "DEPART".equalsIgnoreCase(c.getType().toString())
                            && c.getDateRegistre() != null
                            && c.getDateRegistre().getMonthValue() == month.getMonthValue()
                            && c.getDateRegistre().getYear() == month.getYear())
                    .count();
            monthlyArrivees.add(countArr);
            monthlyDeparts.add(countDep);
        }
        dto.setMonthlyLabels(monthLabels);
        dto.setMonthlyArrivees(monthlyArrivees);
        dto.setMonthlyDeparts(monthlyDeparts);

        // 5. Confidentiality distribution
        Map<String, Integer> confCounts = confidentialiteRepository.findAll().stream()
                .collect(Collectors.toMap(
                        Confidentialite::getNom,
                        conf -> (int) courriers.stream()
                                .filter(c -> c.getDegreConfiden() != null && c.getDegreConfiden().getId()== conf.getId())
                                .count()
                ));

        dto.setConfidentialiteCounts(confCounts);


        // 6. Courriers par service (by service name)
        Map<String, Map<String, Integer>> courriersByService =
                courriers.stream()
                        .filter(c -> c.getService() != null)
                        .collect(Collectors.groupingBy(
                                c -> c.getService().getNom(),
                                Collectors.groupingBy(
                                        c -> c.getType() == TypeCourrier.ARRIVEE ? "arrivee" : "depart",
                                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                                )
                        ));

        // make sure every service has both keys, even if zero
        courriersByService.values()
                .forEach(m -> {
                    m.putIfAbsent("arrivee", 0);
                    m.putIfAbsent("depart",  0);
                });

        // set on your DTO
        dto.setCourriersByService(courriersByService);

        // 7. Courriers traités par employé (by employee full name)
        Map<String, Integer> byEmploye = courriers.stream()
                .filter(c -> c.getEmploye() != null)
                .collect(Collectors.groupingBy(
                        c -> c.getEmploye().getPrenom() + " " + c.getEmploye().getNom(),
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));
        dto.setCourriersByEmploye(byEmploye);

        // 8. Temps moyen de traitement (en jours) sur 30 derniers jours
        LocalDate limit = LocalDate.now().minusDays(30);
        List<Long> traitementJours = courriers.stream()
                .filter(c -> c.getDateArrive() != null && c.getDateTraitement() != null)
                .filter(c -> c.getDateArrive().isAfter(limit) || c.getDateArrive().isEqual(limit))
                .map(c -> {
                    LocalDate arrive = c.getDateArrive();
                    LocalDate traite = c.getDateTraitement();
                    return (long) Period.between(arrive, traite).getDays();
                })
                .filter(j -> j >= 0)
                .collect(Collectors.toList());
        double traitementMoyen = traitementJours.isEmpty() ? 0.0 :
                traitementJours.stream().mapToLong(Long::longValue).average().orElse(0.0);
        dto.setTraitementMoyenJours(Math.round(traitementMoyen * 10.0) / 10.0);

        return dto;
    }

    public boolean adminBCUpdateCourrier(AdminBCUpdateCourrierDTO dto) {
        Courrier courrier = courrierRepository.findById(dto.getCourrierId()).orElse(null);
        if (courrier == null) return false;

        // Update Service
        if (dto.getServiceId() != null) {
            serviceInternRepository.findById(dto.getServiceId()).ifPresent(courrier::setService);
        }

        // Update Urgence
        if (dto.getUrgence() != null ) {
            urgenceRepository.findById(dto.getUrgence()).ifPresent(courrier::setUrgence);

        }

        // Update Confidentialite
        if (dto.getConfidentialite() != null) {
            confidentialiteRepository.findById(dto.getConfidentialite()).ifPresent(courrier::setDegreConfiden);
        }

        courrierRepository.save(courrier);
        return true;
    }



}