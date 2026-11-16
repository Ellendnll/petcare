package com.petcare.vacina;

import java.util.List;
import java.util.Map;

public final class VacinaCatalogo {

    public record Tipo(String codigo, String nome) {
    }

    private static final List<Tipo> CACHORRO = List.of(
            new Tipo("v10", "V10 (múltipla canina)"),
            new Tipo("antirrabica", "Antirrábica"),
            new Tipo("giardia", "Giárdia"),
            new Tipo("tosse_dos_canis", "Tosse dos canis"),
            new Tipo("leishmaniose", "Leishmaniose")
    );

    private static final List<Tipo> GATO = List.of(
            new Tipo("v4", "V4 (quádrupla felina)"),
            new Tipo("antirrabica", "Antirrábica"),
            new Tipo("felv", "Leucemia felina (FeLV)"),
            new Tipo("giardia", "Giárdia")
    );

    private static final List<Tipo> AVE = List.of(
            new Tipo("poliomavirus", "Poliomavírus"),
            new Tipo("pacheco", "Doença de Pacheco"),
            new Tipo("variola_aviaria", "Varíola aviária")
    );

    private static final Map<EspecieVacina, List<Tipo>> CATALOGO = Map.of(
            EspecieVacina.CACHORRO, CACHORRO,
            EspecieVacina.GATO, GATO,
            EspecieVacina.LORO, AVE,
            EspecieVacina.CALOPSITA, AVE,
            EspecieVacina.HAMSTER, List.of()
    );

    private VacinaCatalogo() {
    }

    public static List<Tipo> porEspecie(EspecieVacina especie) {
        return CATALOGO.getOrDefault(especie, List.of());
    }

    public static String observacao(EspecieVacina especie) {
        if (especie == EspecieVacina.HAMSTER) {
            return "Hamsters não possuem vacinas de rotina obrigatórias.";
        }
        return null;
    }
}
