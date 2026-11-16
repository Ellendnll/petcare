package com.petcare.vacina;

import com.petcare.common.exception.RegraNegocioException;

import java.text.Normalizer;
import java.util.Locale;

public enum EspecieVacina {
    CACHORRO,
    GATO,
    LORO,
    CALOPSITA,
    HAMSTER;

    public static EspecieVacina da(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new RegraNegocioException("A espécie do pet é obrigatória para registrar a vacinação");
        }

        String normalizada = Normalizer.normalize(valor, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .trim();

        return switch (normalizada) {
            case "cachorro", "cao", "dog" -> CACHORRO;
            case "gato", "cat" -> GATO;
            case "loro", "papagaio", "parrot" -> LORO;
            case "calopsita", "cockatiel" -> CALOPSITA;
            case "hamster" -> HAMSTER;
            default -> throw new RegraNegocioException("Espécie sem protocolo de vacinação: " + valor);
        };
    }
}
