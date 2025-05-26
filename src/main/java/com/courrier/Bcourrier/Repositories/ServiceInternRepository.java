package com.courrier.Bcourrier.Repositories;

import com.courrier.Bcourrier.Entities.ServiceIntern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceInternRepository extends JpaRepository<ServiceIntern, Long> {
    List<ServiceIntern> findAllByOrderByNomAsc();
    List<ServiceIntern> findAllByIdIn(List<Long> ids);
    ServiceIntern findByNom(String nom);

}