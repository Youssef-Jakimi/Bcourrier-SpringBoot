package com.courrier.Bcourrier.Repositories;

import com.courrier.Bcourrier.Entities.Confidentialité;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfidentialitéRepository extends JpaRepository<Confidentialité, Long> {
}