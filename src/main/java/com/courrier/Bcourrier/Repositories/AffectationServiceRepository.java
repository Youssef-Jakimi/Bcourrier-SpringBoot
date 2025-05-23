package com.courrier.Bcourrier.Repositories;

import com.courrier.Bcourrier.Entities.AffectationService;
import com.courrier.Bcourrier.Entities.Depart;
import com.courrier.Bcourrier.Entities.Employe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AffectationServiceRepository extends JpaRepository<AffectationService, Integer> {
    Optional<AffectationService> findTopByEmployeOrderByDateAffectationAsc(Employe employe);

}
