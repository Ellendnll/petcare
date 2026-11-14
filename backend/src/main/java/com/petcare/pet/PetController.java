package com.petcare.pet;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pets")
@CrossOrigin(origins = "*") 
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @GetMapping
    public List<Pet> listar() {
        return petService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pet> buscarPorId(@PathVariable Long id) {
        return petService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Pet> cadastrar(@Valid @RequestBody Pet pet) {
        Pet novoPet = petService.salvar(pet);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoPet);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pet> atualizar(@PathVariable Long id, @Valid @RequestBody Pet pet) {
        try {
            Pet petAtualizado = petService.atualizar(id, pet);
            return ResponseEntity.ok(petAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (petService.buscarPorId(id).isPresent()) {
            petService.deletar(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}