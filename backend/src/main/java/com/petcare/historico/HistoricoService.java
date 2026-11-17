package com.petcare.historico;

import com.petcare.common.exception.RecursoNaoEncontradoException;
import com.petcare.consulta.ConsultaRepository;
import com.petcare.pet.Pet;
import com.petcare.pet.PetRepository;
import com.petcare.vacina.VacinaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoricoService {

    private final PetRepository petRepository;
    private final VacinaRepository vacinaRepository;
    private final ConsultaRepository consultaRepository;

    @Transactional(readOnly = true)
    public HistoricoDTO buscar(Long petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Animal não encontrado: " + petId));

        List<HistoricoDTO.Evento> eventos = new ArrayList<>();
        vacinaRepository.findByPetIdOrderByDataAplicacaoDesc(petId).forEach(v ->
                eventos.add(new HistoricoDTO.Evento("VACINA", v.getDataAplicacao().atStartOfDay(),
                        v.getNome(), v.getObservacoes())));
        consultaRepository.findByPetIdOrderByDataHoraAsc(petId).forEach(c ->
                eventos.add(new HistoricoDTO.Evento("CONSULTA", c.getDataHora(), c.getMotivo(), c.getClinica())));

        eventos.sort(Comparator.comparing(HistoricoDTO.Evento::data).reversed());
        return new HistoricoDTO(pet.getId(), pet.getNome(), eventos);
    }
}