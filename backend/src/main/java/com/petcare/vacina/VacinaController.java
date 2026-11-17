package com.petcare.vacina;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/vacinas")
@RequiredArgsConstructor
public class VacinaController {

    private final VacinaService service;

    @PostMapping
    public ResponseEntity<VacinaResponseDTO> criar(@Valid @RequestBody VacinaRequestDTO dto) {
        VacinaResponseDTO criada = service.criar(dto);
        return ResponseEntity.created(URI.create("/api/vacinas/" + criada.id())).body(criada);
    }

    @GetMapping
    public List<VacinaResponseDTO> listar(@RequestParam(required = false) Long petId) {
        return service.listar(petId);
    }

    @GetMapping("/{id}")
    public VacinaResponseDTO buscar(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public VacinaResponseDTO atualizar(@PathVariable Long id, @Valid @RequestBody VacinaRequestDTO dto) {
        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}