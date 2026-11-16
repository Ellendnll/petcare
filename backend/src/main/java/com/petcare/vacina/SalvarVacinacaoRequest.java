package com.petcare.vacina;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SalvarVacinacaoRequest(
        @NotNull(message = "Informe as vacinas marcadas")
        List<@NotBlank(message = "O código da vacina é obrigatório") String> aplicadas
) {
}
