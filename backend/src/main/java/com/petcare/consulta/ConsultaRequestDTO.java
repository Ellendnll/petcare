package com.petcare.consulta;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ConsultaRequestDTO(
        @NotBlank(message = "A clínica é obrigatória") String clinica,
        @NotNull(message = "A data e o horário são obrigatórios")
        @Future(message = "A consulta deve ser agendada para o futuro") LocalDateTime dataHora,
        @NotBlank(message = "O motivo é obrigatório") String motivo,
        @NotNull(message = "O animal é obrigatório") Long petId
) {
}