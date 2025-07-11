package com.courrier.Bcourrier.Repositories;
import com.courrier.Bcourrier.Entities.Courrier;
import com.courrier.Bcourrier.Entities.ServiceIntern;
import com.courrier.Bcourrier.Enums.TypeCourrier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CourrierRepository extends JpaRepository<Courrier, Integer> {
    List<Courrier> findByType(TypeCourrier type);
    List<Courrier> findByService(ServiceIntern serviceIntern);
    long countByArchiverTrue();
    long countByDateTraitementNotNull();
    long countByDateTraitementNull();

    List<Courrier> findTop3ByArchiverTrueOrderByDateRegistreDesc();

    @Query("SELECT FUNCTION('DATE_FORMAT', c.dateRegistre, '%Y-%m') as month, COUNT(c) FROM Courrier c WHERE c.archiver = true GROUP BY month")
    List<Object[]> countArchivedCourriersPerMonthRaw();

    @Query("SELECT numeroRegistre FROM Courrier")
    List<Integer> listCourrier();

    List<Courrier> findByServiceAndTypeAndArchiverFalse(ServiceIntern service, TypeCourrier type);
    List<Courrier> findByServiceAndTypeAndArchiverTrue(ServiceIntern service, TypeCourrier type);
    Long countByServiceAndTypeAndArchiverFalse(ServiceIntern service, TypeCourrier type);
    Long countByServiceAndTypeAndArchiverTrue(ServiceIntern service, TypeCourrier type);

    List<Courrier> findTop3ByEmploye_LoginOrderByDateRegistreDesc(String login);

}