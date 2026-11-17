package com.petcare.historico;

import java.time.LocalDateTime;
import java.util.List;

public record HistoricoDTO(Long petId, String petNome, List<Evento> eventos) {

    public record Evento(String tipo, LocalDateTime data, String titulo, String detalhe) {
    }
}