package com.courrier.Bcourrier.Repositories;

import com.courrier.Bcourrier.Entities.AffectationCourrierService;
import com.courrier.Bcourrier.Enums.TypeCourrier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AffectationCourrierServiceRepository extends JpaRepository<AffectationCourrierService, Long> {
    List<AffectationCourrierService> findByCourrier_Type(TypeCourrier type);
}
