package com.courrier.Bcourrier.Repositories;

import com.courrier.Bcourrier.Entities.Courrier;
import com.courrier.Bcourrier.Enums.TypeCourrier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface AdminBcRepository extends JpaRepository<Courrier, Long> {

    long countByTypeAndArchiverFalse(TypeCourrier type);

    long countByTypeAndArchiverTrue(TypeCourrier type);

    List<Courrier> findTop3ByOrderByDateRegistreDesc();

    @Query("SELECT c.dateRegistre, COUNT(c) FROM Courrier c WHERE c.dateRegistre IS NOT NULL GROUP BY FUNCTION('YEAR', c.dateRegistre), FUNCTION('MONTH', c.dateRegistre)")
    List<Object[]> countCourriersPerMonthRaw();


    default Map<String, Long> countCourriersPerMonth() {
            Map<String, Long> map = new LinkedHashMap<>();
            for (Object[] obj : countCourriersPerMonthRaw()) {
                map.put((String) obj[0], (Long) obj[1]);
            }
            return map;
        }
}