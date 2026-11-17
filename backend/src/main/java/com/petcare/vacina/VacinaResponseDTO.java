package com.petcare.vacina;

import java.time.LocalDate;

public record VacinaResponseDTO(
        Long id, String nome, LocalDate dataAplicacao, LocalDate proximaDose,
        String observacoes, Long petId, String petNome
) {
    public static VacinaResponseDTO de(Vacina v) {
        return new VacinaResponseDTO(v.getId(), v.getNome(), v.getDataAplicacao(), v.getProximaDose(),
                v.getObservacoes(), v.getPet().getId(), v.getPet().getNome());
    }
}