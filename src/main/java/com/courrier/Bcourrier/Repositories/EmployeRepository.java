package com.courrier.Bcourrier.Repositories;
import com.courrier.Bcourrier.Entities.Employe;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeRepository extends JpaRepository<Employe, Long> {
    // Used in /verify endpoint
    Optional<Employe> findByVerificationToken(String token);

    // You will use this in your login logic later
    Optional<Employe> findByLogin(String login);
    long countByActiveFalse(); // or countByCheckEmailFalse() based on your logic
    List<Employe> findByActiveTrue();
    List<Employe> findByActiveFalse();
    @Override
    List<Employe> findAll();


    // Optional: to avoid duplicates
    boolean existsByEmail(String email);

    boolean existsByLogin(String login);
}
