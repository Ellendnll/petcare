package com.petcare.consulta;

import com.petcare.common.exception.RecursoNaoEncontradoException;
import com.petcare.common.exception.RegraNegocioException;
import com.petcare.pet.Pet;
import com.petcare.pet.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultaService {

    private final ConsultaRepository consultaRepository;
    private final PetRepository petRepository;

    @Transactional
    public ConsultaResponseDTO criar(ConsultaRequestDTO dto) {
        Pet pet = buscarPet(dto.petId());

        if (consultaRepository.existsByPetIdAndDataHora(pet.getId(), dto.dataHora())) {
            throw new RegraNegocioException("O animal já possui uma consulta nesse horário");
        }

        Consulta consulta = new Consulta();
        preencher(consulta, dto, pet);
        return ConsultaResponseDTO.de(consultaRepository.save(consulta));
    }

    @Transactional(readOnly = true)
    public ConsultaResponseDTO buscarPorId(Long id) {
        return ConsultaResponseDTO.de(buscarConsulta(id));
    }

    @Transactional(readOnly = true)
    public List<ConsultaResponseDTO> listar(Long petId, LocalDate data) {
        List<Consulta> consultas;

        if (petId != null && data != null) {
            consultas = consultaRepository.findByPetIdAndDataHoraBetweenOrderByDataHoraAsc(
                    petId, inicioDoDia(data), fimDoDia(data));
        } else if (petId != null) {
            consultas = consultaRepository.findByPetIdOrderByDataHoraAsc(petId);
        } else if (data != null) {
            consultas = consultaRepository.findByDataHoraBetweenOrderByDataHoraAsc(
                    inicioDoDia(data), fimDoDia(data));
        } else {
            consultas = consultaRepository.findAllByOrderByDataHoraAsc();
        }

        return consultas.stream().map(ConsultaResponseDTO::de).toList();
    }

    @Transactional
    public ConsultaResponseDTO atualizar(Long id, ConsultaRequestDTO dto) {
        Consulta consulta = buscarConsulta(id);
        Pet pet = buscarPet(dto.petId());

        if (consultaRepository.existsByPetIdAndDataHoraAndIdNot(pet.getId(), dto.dataHora(), id)) {
            throw new RegraNegocioException("O animal já possui uma consulta nesse horário");
        }

        preencher(consulta, dto, pet);
        return ConsultaResponseDTO.de(consulta);
    }

    @Transactional
    public void cancelar(Long id) {
        consultaRepository.delete(buscarConsulta(id));
    }

    private Consulta buscarConsulta(Long id) {
        return consultaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Consulta não encontrada: " + id));
    }

    private Pet buscarPet(Long petId) {
        return petRepository.findById(petId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Animal não encontrado: " + petId));
    }

    private void preencher(Consulta consulta, ConsultaRequestDTO dto, Pet pet) {
        consulta.setClinica(dto.clinica());
        consulta.setDataHora(dto.dataHora());
        consulta.setMotivo(dto.motivo());
        consulta.setPet(pet);
    }

    private LocalDateTime inicioDoDia(LocalDate data) {
        return data.atStartOfDay();
    }

    private LocalDateTime fimDoDia(LocalDate data) {
        return data.atTime(LocalTime.MAX);
    }
}