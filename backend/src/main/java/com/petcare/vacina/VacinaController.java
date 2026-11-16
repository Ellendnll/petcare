package com.petcare.vacina;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/vacinas")
@RequiredArgsConstructor
public class VacinaController {

    private final VacinaService service;

    @GetMapping("/pets")
    public List<PetVacinaResumoDTO> buscar(@RequestParam Long usuarioId, @RequestParam String nome) {
        return service.buscar(usuarioId, nome);
    }

    @GetMapping("/pets/{petId}")
    public PetVacinaDetalheDTO detalhar(@PathVariable Long petId) {
        return service.detalhar(petId);
    }

    @PutMapping("/pets/{petId}")
    public PetVacinaDetalheDTO salvar(@PathVariable Long petId,
                                      @Valid @RequestBody SalvarVacinacaoRequest request) {
        return service.salvar(petId, request);
    }
}
