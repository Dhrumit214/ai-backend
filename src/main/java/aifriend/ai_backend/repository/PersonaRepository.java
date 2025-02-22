package aifriend.ai_backend.repository;

import aifriend.ai_backend.model.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, Long> {
    List<Persona> findByRequiredPlan(Persona.PlanType requiredPlan);
    List<Persona> findByIsActiveTrue();
    Persona findByName(String name);
}
