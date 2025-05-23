// src/main/java/com/courrier/Bcourrier/Repositories/DepartRepository.java
package com.courrier.Bcourrier.Repositories;

import com.courrier.Bcourrier.Entities.Depart;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DepartRepository extends JpaRepository<Depart, Integer> {
    List<Depart> findAll();
}
