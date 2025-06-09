package com.courrier.Bcourrier.Services;

import com.courrier.Bcourrier.DTO.AdminBC.AdminBCDashboardDTO;
import com.courrier.Bcourrier.DTO.AdminBC.CourrierArriveeDTO;
import com.courrier.Bcourrier.DTO.AdminBC.CourrierDepartDTO;
import com.courrier.Bcourrier.DTO.AdminBC.StatsDTO;
import com.courrier.Bcourrier.Entities.Courrier;
import com.courrier.Bcourrier.Enums.Confidentialite;
import com.courrier.Bcourrier.Enums.TypeCourrier;
import com.courrier.Bcourrier.Enums.Urgence;
import com.courrier.Bcourrier.Repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DelegueService {
    @Autowired
    private final DelegueRepository delegueRepository;
    private final EmployeRepository employeRepository;
    @Autowired
    private final DepartRepository departRepository;
    @Autowired
    private final CourrierRepository courrierRepository;
    @Autowired
    private final ServiceInternRepository serviceInternRepository;

    public AdminBCDashboardDTO getAdminDashboardData() {
        AdminBCDashboardDTO dto = new AdminBCDashboardDTO();

        // ─── Totals ───
        dto.setTotalCourriersArrivee(
                delegueRepository.countByTypeAndArchiverFalse(TypeCourrier.ARRIVEE)
        );
        dto.setTotalCourriersDepart(
                delegueRepository.countByTypeAndArchiverFalse(TypeCourrier.DEPART)
        );
        dto.setTotalArriveeArchives(
                delegueRepository.countByTypeAndArchiverTrue(TypeCourrier.ARRIVEE)
        );
        dto.setTotalDepartArchives(
                delegueRepository.countByTypeAndArchiverTrue(TypeCourrier.DEPART)
        );

        // ─── Last 3 courriers ───
        List<Map<String, Object>> last3 = delegueRepository.findTop3ByOrderByDateRegistreDesc()
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

    public List<CourrierArriveeDTO> getAllArriveeCourrierDTOs() {
        return courrierRepository.findByType(TypeCourrier.ARRIVEE)
                .stream()
                .map(c -> {
                    CourrierArriveeDTO dto = new CourrierArriveeDTO();
                    dto.setId(c.getId());
                    dto.setObject(c.getObject());
                    dto.setDescription(c.getDescription());
                    dto.setStatutCourrier(String.valueOf(c.getStatutCourrier()));
                    dto.setDateArrive(c.getDateArrive() != null ? c.getDateArrive().toString() : null);
                    dto.setDateTraitement(c.getDateTraitement() != null ? c.getDateTraitement().toString() : null);
                    dto.setNumeroRegistre(c.getNumeroRegistre());
                    dto.setDateRegistre(c.getDateRegistre() != null ? c.getDateRegistre().toString() : null);
                    dto.setSignataire(c.getSignataire());
                    dto.setArchiver(c.isArchiver());
                    dto.setService(c.getService() != null ? c.getService().getNom() : null);
                    dto.setEmploye(c.getEmploye() != null ? c.getEmploye().getPrenom() + " " + c.getEmploye().getNom() : null);
                    dto.setNature(c.getNature());
                    dto.setDegreConfiden(c.getDegreConfiden() != null ? c.getDegreConfiden().toString() : null);
                    dto.setUrgence(c.getUrgence() != null ? c.getUrgence().toString() : null);
                    dto.setMotDes(c.getMotDes());
                    return dto;
                })
                .toList();
    }

    public List<CourrierDepartDTO> getAllDepartCourrierDTOs() {
        return departRepository.findAll()
                .stream()
                .map(d -> {
                    CourrierDepartDTO dto = new CourrierDepartDTO();
                    dto.setId(d.getId());
                    dto.setObject(d.getCourrier().getObject());
                    dto.setDescription(d.getCourrier().getDescription());
                    dto.setStatutCourrier(String.valueOf(d.getCourrier().getStatutCourrier()));
                    dto.setDateDepart(d.getCourrier().getDateRegistre() != null ? d.getCourrier().getDateRegistre().toString() : null);
                    dto.setNumeroRegistre(d.getCourrier().getNumeroRegistre());
                    dto.setArchiver(d.getCourrier().isArchiver());
                    dto.setService(d.getCourrier().getService() != null ? d.getCourrier().getService().getNom() : null);
                    dto.setEmploye(d.getCourrier().getEmploye() != null ? d.getCourrier().getEmploye().getPrenom() + " " + d.getCourrier().getEmploye().getNom() : null);
                    dto.setNomExpediteur(d.getNomExpediteur());
                    dto.setVoieExpedition(d.getVoieExpedition() != null ? d.getVoieExpedition().toString() : null);
                    dto.setNature(d.getCourrier().getNature());
                    dto.setDegreConfiden(d.getCourrier().getDegreConfiden() != null ? d.getCourrier().getDegreConfiden().toString() : null);
                    dto.setUrgence(d.getCourrier().getUrgence() != null ? d.getCourrier().getUrgence().toString() : null);
                    dto.setMotDes(d.getCourrier().getMotDes());
                    return dto;
                })
                .toList();
    }

    public StatsDTO getStats() {
        StatsDTO dto = new StatsDTO();

        // 1. Total employees
        dto.setTotalEmployes((int) employeRepository.count());

        // 2. Total active services
        dto.setTotalServices((int) serviceInternRepository.count());

        // 3. Courriers urgents
        int urgentCount = (int) courrierRepository.findAll().stream()
                .filter(c -> c.getUrgence() == Urgence.URGENT)
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
        Map<String, Integer> confCounts = Arrays.stream(Confidentialite.values())
                .collect(Collectors.toMap(
                        Enum::name,
                        conf -> (int) courriers.stream().filter(c -> c.getDegreConfiden() == conf).count()
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
                    LocalDate traite = c.getDateTraitement().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    return (long) Period.between(arrive, traite).getDays();
                })
                .filter(j -> j >= 0)
                .collect(Collectors.toList());
        double traitementMoyen = traitementJours.isEmpty() ? 0.0 :
                traitementJours.stream().mapToLong(Long::longValue).average().orElse(0.0);
        dto.setTraitementMoyenJours(Math.round(traitementMoyen * 10.0) / 10.0);

        return dto;
    }




}