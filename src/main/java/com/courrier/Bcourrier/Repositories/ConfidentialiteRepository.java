package com.courrier.Bcourrier.Repositories;

import com.courrier.Bcourrier.Entities.Confidentialite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConfidentialiteRepository extends JpaRepository<Confidentialite, Long> {
    List<Confidentialite> findByNom (String nom);
        Confidentialite findTopByNomOrderByIdDesc (String nom);
}