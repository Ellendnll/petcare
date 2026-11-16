package com.petcare.vacina;

import com.petcare.pet.Pet;

public record PetVacinaResumoDTO(
        Long id,
        String nome,
        String especie,
        String raca,
        Long usuarioId
) {
    public static PetVacinaResumoDTO de(Pet pet) {
        return new PetVacinaResumoDTO(
                pet.getId(),
                pet.getNome(),
                pet.getEspecie(),
                pet.getRaca(),
                pet.getUsuarioId()
        );
    }
}
