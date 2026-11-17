package com.petcare.usuario;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService service;

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> cadastrar(@Valid @RequestBody UsuarioRequestDTO dto) {
        UsuarioResponseDTO criado = service.cadastrar(dto);
        return ResponseEntity.created(URI.create("/api/usuarios/" + criado.id())).body(criado);
    }

    @PostMapping("/login")
    public UsuarioResponseDTO login(@Valid @RequestBody LoginRequestDTO dto) {
        return service.login(dto);
    }

    @GetMapping("/{id}")
    public UsuarioResponseDTO buscar(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public UsuarioResponseDTO atualizar(@PathVariable Long id,
                                        @Valid @RequestBody UsuarioAtualizacaoDTO dto) {
        return service.atualizar(id, dto);
    }
}
