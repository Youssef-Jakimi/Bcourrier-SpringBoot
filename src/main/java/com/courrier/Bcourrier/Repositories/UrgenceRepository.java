package com.courrier.Bcourrier.Repositories;

import com.courrier.Bcourrier.Entities.Urgence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UrgenceRepository extends JpaRepository<Urgence, Long> {
    List<Urgence> findByNom(String nom);
    Urgence findTopByNomOrderByIdDesc(String nom);

}