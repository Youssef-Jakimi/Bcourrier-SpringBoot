package com.courrier.Bcourrier.Repositories;

import com.courrier.Bcourrier.Entities.Question;
import com.courrier.Bcourrier.Entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    Optional<Question> findByNom(String name);

}
