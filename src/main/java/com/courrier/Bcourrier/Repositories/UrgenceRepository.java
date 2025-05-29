package com.courrier.Bcourrier.Repositories;

import com.courrier.Bcourrier.Entities.Urgence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrgenceRepository extends JpaRepository<Urgence, Long> {
}