package com.petcare.consulta;

import com.petcare.common.exception.RecursoNaoEncontradoException;
import com.petcare.common.exception.RegraNegocioException;
import com.petcare.pet.Pet;
import com.petcare.pet.PetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultaServiceTest {

    @Mock
    private ConsultaRepository consultaRepository;

    @Mock
    private PetRepository petRepository;

    @InjectMocks
    private ConsultaService service;

    private ConsultaRequestDTO dtoValido() {
        return new ConsultaRequestDTO("VetCare", LocalDateTime.now().plusDays(3), "Check-up", 1L);
    }

    private Pet petExistente() {
        Pet pet = new Pet();
        pet.setId(1L);
        pet.setNome("Rex");
        return pet;
    }

    @Test
    void criar_comDadosValidos_salvaConsulta() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(petExistente()));
        when(consultaRepository.existsByPetIdAndDataHora(eq(1L), any())).thenReturn(false);
        when(consultaRepository.save(any(Consulta.class))).thenAnswer(i -> i.getArgument(0));

        ConsultaResponseDTO resposta = service.criar(dtoValido());

        assertEquals("VetCare", resposta.clinica());
        assertEquals(1L, resposta.petId());
        verify(consultaRepository).save(any(Consulta.class));
    }

    @Test
    void criar_comPetInexistente_lancaNaoEncontrado() {
        when(petRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> service.criar(dtoValido()));
        verify(consultaRepository, never()).save(any());
    }

    @Test
    void criar_comHorarioDuplicado_lancaRegraNegocio() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(petExistente()));
        when(consultaRepository.existsByPetIdAndDataHora(eq(1L), any())).thenReturn(true);

        assertThrows(RegraNegocioException.class, () -> service.criar(dtoValido()));
        verify(consultaRepository, never()).save(any());
    }

    @Test
    void cancelar_comIdInexistente_lancaNaoEncontrado() {
        when(consultaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> service.cancelar(99L));
    }
}