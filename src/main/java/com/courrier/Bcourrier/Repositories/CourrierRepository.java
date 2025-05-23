package com.courrier.Bcourrier.Repositories;
import com.courrier.Bcourrier.Entities.Courrier;
import com.courrier.Bcourrier.Enums.TypeCourrier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourrierRepository extends JpaRepository<Courrier, Long> {
    List<Courrier> findByType(TypeCourrier type);


}
