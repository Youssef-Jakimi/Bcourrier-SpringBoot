package com.courrier.Bcourrier.Repositories;

import com.courrier.Bcourrier.Entities.AffectationCourrierEmploye;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AffectationCourrierEmployeRepository extends JpaRepository<AffectationCourrierEmploye, Integer> {
    List<AffectationCourrierEmploye> findByCourrier_ArchiverTrue();
    boolean existsByCourrier_Id(int courrierId);

}
