package com.courrier.Bcourrier.Repositories;
import com.courrier.Bcourrier.Entities.Employe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeRepository extends JpaRepository<Employe, Long> {
    // Used in /verify endpoint
    Optional<Employe> findByVerificationToken(String token);

    // You will use this in your login logic later
    Optional<Employe> findByLogin(String login);

    // Optional: to avoid duplicates
    boolean existsByEmail(String email);

    boolean existsByLogin(String login);
}
