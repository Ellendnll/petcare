package com.petcare.consulta;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    List<Consulta> findAllByOrderByDataHoraAsc();

    List<Consulta> findByPetIdOrderByDataHoraAsc(Long petId);

    List<Consulta> findByDataHoraBetweenOrderByDataHoraAsc(LocalDateTime inicio, LocalDateTime fim);

    List<Consulta> findByPetIdAndDataHoraBetweenOrderByDataHoraAsc(
            Long petId, LocalDateTime inicio, LocalDateTime fim);

    boolean existsByPetIdAndDataHora(Long petId, LocalDateTime dataHora);

    boolean existsByPetIdAndDataHoraAndIdNot(Long petId, LocalDateTime dataHora, Long id);
}