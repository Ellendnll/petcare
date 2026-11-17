package com.petcare.vacina;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record VacinaRequestDTO(
        @NotBlank(message = "O nome da vacina é obrigatório") String nome,
        @NotNull(message = "A data de aplicação é obrigatória")
        @PastOrPresent(message = "A data de aplicação não pode ser futura") LocalDate dataAplicacao,
        LocalDate proximaDose,
        @Size(max = 500, message = "Observações: máximo de 500 caracteres") String observacoes,
        @NotNull(message = "O animal é obrigatório") Long petId
) {
}
