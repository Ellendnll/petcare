package com.petcare.vacina;

import java.time.LocalDate;
import java.util.List;

public record PetVacinaDetalheDTO(
        Long id,
        String nome,
        String especie,
        String raca,
        LocalDate nascimento,
        Long usuarioId,
        String observacao,
        List<VacinaStatusDTO> vacinas
) {
}
