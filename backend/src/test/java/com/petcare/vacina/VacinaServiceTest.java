package com.petcare.vacina;

import com.petcare.common.exception.RecursoNaoEncontradoException;
import com.petcare.common.exception.RegraNegocioException;
import com.petcare.pet.Pet;
import com.petcare.pet.PetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VacinaServiceTest {

    @Mock
    private VacinaRepository vacinaRepository;
    @Mock
    private PetRepository petRepository;
    @InjectMocks
    private VacinaService service;

    private Pet pet() {
        Pet pet = new Pet();
        pet.setId(1L);
        pet.setNome("Rex");
        return pet;
    }

    @Test
    void criaVacinaComDadosValidos() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(pet()));
        when(vacinaRepository.save(any(Vacina.class))).thenAnswer(i -> i.getArgument(0));

        VacinaRequestDTO dto = new VacinaRequestDTO(
                "  V10 ", LocalDate.of(2026, 1, 10), LocalDate.of(2027, 1, 10), "Sem reação", 1L);

        VacinaResponseDTO resposta = service.criar(dto);

        assertThat(resposta.nome()).isEqualTo("V10");
        assertThat(resposta.petNome()).isEqualTo("Rex");
        assertThat(resposta.proximaDose()).isEqualTo(LocalDate.of(2027, 1, 10));
    }

    @Test
    void rejeitaProximaDoseAnteriorAAplicacao() {
        VacinaRequestDTO dto = new VacinaRequestDTO(
                "V10", LocalDate.of(2026, 5, 10), LocalDate.of(2026, 5, 1), null, 1L);

        assertThatThrownBy(() -> service.criar(dto))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("próxima dose");
        verify(vacinaRepository, never()).save(any());
    }

    @Test
    void rejeitaVacinaParaAnimalInexistente() {
        when(petRepository.findById(99L)).thenReturn(Optional.empty());

        VacinaRequestDTO dto = new VacinaRequestDTO("V10", LocalDate.of(2026, 1, 10), null, null, 99L);

        assertThatThrownBy(() -> service.criar(dto))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    void excluirVacinaInexistenteLancaErro() {
        when(vacinaRepository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.excluir(5L))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    void listaSemFiltroUsaTodasAsVacinas() {
        service.listar(null);

        verify(vacinaRepository).findAllByOrderByDataAplicacaoDesc();
        verify(vacinaRepository, never()).findByPetIdOrderByDataAplicacaoDesc(any());
    }
}