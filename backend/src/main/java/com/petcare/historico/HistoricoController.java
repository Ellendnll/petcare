package com.petcare.historico;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pets/{petId}/historico")
@RequiredArgsConstructor
public class HistoricoController {

    private final HistoricoService service;

    @GetMapping
    public HistoricoDTO buscar(@PathVariable Long petId) {
        return service.buscar(petId);
    }
}
