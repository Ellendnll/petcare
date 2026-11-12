package com.petcare.consulta;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/consultas")
@RequiredArgsConstructor
public class ConsultaController {

    private final ConsultaService service;

    @PostMapping
    public ResponseEntity<ConsultaResponseDTO> criar(@Valid @RequestBody ConsultaRequestDTO dto) {
        ConsultaResponseDTO criada = service.criar(dto);
        return ResponseEntity.created(URI.create("/consultas/" + criada.id())).body(criada);
    }

    @GetMapping("/{id}")
    public ConsultaResponseDTO buscar(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @GetMapping
    public List<ConsultaResponseDTO> listar(
            @RequestParam(required = false) Long petId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        return service.listar(petId, data);
    }

    @PutMapping("/{id}")
    public ConsultaResponseDTO atualizar(@PathVariable Long id,
                                         @Valid @RequestBody ConsultaRequestDTO dto) {
        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        service.cancelar(id);
        return ResponseEntity.noContent().build();
    }
}