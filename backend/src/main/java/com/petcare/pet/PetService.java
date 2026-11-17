package com.petcare.pet;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import com.petcare.consulta.ConsultaRepository;
import com.petcare.vacina.VacinaRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PetService {

    private final PetRepository petRepository;
    private final ConsultaRepository consultaRepository;
    private final VacinaRepository vacinaRepository;

    public PetService(PetRepository petRepository,
    ConsultaRepository consultaRepository,
    VacinaRepository vacinaRepository) {
        this.petRepository = petRepository;
        this.consultaRepository = consultaRepository;
        this.vacinaRepository = vacinaRepository;
}

    public List<Pet> listarTodos() {
        return petRepository.findAll();
    }

    public List<Pet> listarPorUsuario(Long usuarioId) { 
        return petRepository.findByUsuarioId(usuarioId); 
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
            pet.setPeso(petAtualizado.getPeso());
            pet.setFoto(petAtualizado.getFoto());
            pet.setEspecie(petAtualizado.getEspecie());
            pet.setRaca(petAtualizado.getRaca());
            pet.setNascimento(petAtualizado.getNascimento());
            pet.setUsuarioId(petAtualizado.getUsuarioId());
            return petRepository.save(pet);
        }).orElseThrow(() -> new RuntimeException("Pet não encontrado com o ID: " + id));
    }

    @Transactional
    public void deletar(Long id) {
        consultaRepository.deleteByPetId(id);
        vacinaRepository.deleteByPetId(id);
    petRepository.deleteById(id);
}
}