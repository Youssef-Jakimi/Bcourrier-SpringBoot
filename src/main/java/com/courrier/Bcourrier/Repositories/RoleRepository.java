package com.courrier.Bcourrier.Repositories;

import com.courrier.Bcourrier.Entities.Role;
import com.courrier.Bcourrier.Entities.Urgence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByNom(String name);
    Role findTopByNomOrderByIdDesc(String nom);

}