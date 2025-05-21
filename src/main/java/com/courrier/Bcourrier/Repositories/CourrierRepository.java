package com.courrier.Bcourrier.Repositories;
import com.courrier.Bcourrier.Entities.Courrier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourrierRepository extends JpaRepository<Courrier, Long> {
    // You can add custom query methods here if needed
}
