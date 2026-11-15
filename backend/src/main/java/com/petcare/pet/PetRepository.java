package com.petcare.pet;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
    // Método útil para buscar os pets de um tutor específico
    List<Pet> findByUsuarioId(Long usuarioId);
}