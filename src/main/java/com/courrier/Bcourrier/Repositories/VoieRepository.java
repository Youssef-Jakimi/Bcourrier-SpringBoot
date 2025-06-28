package com.courrier.Bcourrier.Repositories;

import com.courrier.Bcourrier.Entities.Urgence;
import com.courrier.Bcourrier.Entities.VoieExpedition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoieRepository extends JpaRepository<VoieExpedition, Long> {

}