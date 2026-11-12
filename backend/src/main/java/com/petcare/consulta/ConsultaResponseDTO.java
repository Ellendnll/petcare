package com.petcare.consulta;

import java.time.LocalDateTime;

public record ConsultaResponseDTO(
        Long id,
        String clinica,
        LocalDateTime dataHora,
        String motivo,
        Long petId,
        String petNome
) {
    public static ConsultaResponseDTO de(Consulta c) {
        return new ConsultaResponseDTO(
                c.getId(),
                c.getClinica(),
                c.getDataHora(),
                c.getMotivo(),
                c.getPet().getId(),
                c.getPet().getNome()
        );
    }
}