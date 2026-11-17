package com.petcare.vacina;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VacinaRepository extends JpaRepository<Vacina, Long> {

    List<Vacina> findAllByOrderByDataAplicacaoDesc();

    List<Vacina> findByPetIdOrderByDataAplicacaoDesc(Long petId);
}