package com.petcare.vacina;

import com.petcare.common.exception.RecursoNaoEncontradoException;
import com.petcare.common.exception.RegraNegocioException;
import com.petcare.pet.Pet;
import com.petcare.pet.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VacinaService {

    private final VacinaRepository vacinaRepository;
    private final PetRepository petRepository;

    @Transactional
    public VacinaResponseDTO criar(VacinaRequestDTO dto) {
        Vacina vacina = new Vacina();
        preencher(vacina, dto);
        return VacinaResponseDTO.de(vacinaRepository.save(vacina));
    }

    @Transactional(readOnly = true)
    public VacinaResponseDTO buscarPorId(Long id) {
        return VacinaResponseDTO.de(buscar(id));
    }

    @Transactional(readOnly = true)
    public List<VacinaResponseDTO> listar(Long petId) {
        List<Vacina> vacinas = petId != null
                ? vacinaRepository.findByPetIdOrderByDataAplicacaoDesc(petId)
                : vacinaRepository.findAllByOrderByDataAplicacaoDesc();
        return vacinas.stream().map(VacinaResponseDTO::de).toList();
    }

    @Transactional
    public VacinaResponseDTO atualizar(Long id, VacinaRequestDTO dto) {
        Vacina vacina = buscar(id);
        preencher(vacina, dto);
        return VacinaResponseDTO.de(vacina);
    }

    @Transactional
    public void excluir(Long id) {
        vacinaRepository.delete(buscar(id));
    }

    private void preencher(Vacina vacina, VacinaRequestDTO dto) {
        if (dto.proximaDose() != null && !dto.proximaDose().isAfter(dto.dataAplicacao())) {
            throw new RegraNegocioException("A próxima dose deve ser posterior à data de aplicação");
        }
        Pet pet = petRepository.findById(dto.petId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Animal não encontrado: " + dto.petId()));
        vacina.setNome(dto.nome().trim());
        vacina.setDataAplicacao(dto.dataAplicacao());
        vacina.setProximaDose(dto.proximaDose());
        vacina.setObservacoes(dto.observacoes());
        vacina.setPet(pet);
    }

    private Vacina buscar(Long id) {
        return vacinaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Vacina não encontrada: " + id));
    }
}