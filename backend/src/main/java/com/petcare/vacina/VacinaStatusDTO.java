package com.petcare.vacina;

public record VacinaStatusDTO(
        String codigo,
        String nome,
        boolean aplicada
) {
}
