package com.courrier.Bcourrier.Repositories;

import com.courrier.Bcourrier.Entities.Courrier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface AdminBcRepository extends JpaRepository<Courrier, Long> {

    long countByTypeAndArchiverFalse(String type);

    long countByTypeAndArchiverTrue(String type);

    List<Courrier> findTop3ByOrderByDateRegistreDesc();

    @Query("SELECT FUNCTION('DATE_FORMAT', c.dateRegistre, '%Y-%m') as month, COUNT(c) FROM Courrier c GROUP BY month")
    List<Object[]> countCourriersPerMonthRaw();


    default Map<String, Long> countCourriersPerMonth() {
            Map<String, Long> map = new LinkedHashMap<>();
            for (Object[] obj : countCourriersPerMonthRaw()) {
                map.put((String) obj[0], (Long) obj[1]);
            }
            return map;
        }
}


