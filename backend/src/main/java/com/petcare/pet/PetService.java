package com.petcare.pet;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PetService {

    private final PetRepository petRepository;

    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    public List<Pet> listarTodos() {
        return petRepository.findAll();
    }

    public Optional<Pet> buscarPorId(Long id) {
        return petRepository.findById(id);
    }

    public Pet salvar(Pet pet) {
        return petRepository.save(pet);
    }

    public Pet atualizar(Long id, Pet petAtualizado) {
        return petRepository.findById(id).map(pet -> {
            pet.setNome(petAtualizado.getNome());
            pet.setEspecie(petAtualizado.getEspecie());
            pet.setRaca(petAtualizado.getRaca());
            pet.setNascimento(petAtualizado.getNascimento());
            pet.setUsuarioId(petAtualizado.getUsuarioId());
            return petRepository.save(pet);
        }).orElseThrow(() -> new RuntimeException("Pet não encontrado com o ID: " + id));
    }

    public void deletar(Long id) {
        petRepository.deleteById(id);
    }
}