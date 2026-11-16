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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VacinaServiceTest {

    @Mock
    private PetRepository petRepository;

    @Mock
    private VacinacaoRepository vacinacaoRepository;

    @InjectMocks
    private VacinaService service;

    @Test
    void catalogo_defineVacinasPorEspecie() {
        assertEquals(List.of("v10", "antirrabica", "giardia", "tosse_dos_canis", "leishmaniose"), codigos(EspecieVacina.CACHORRO));
        assertEquals(List.of("v4", "antirrabica", "felv", "giardia"), codigos(EspecieVacina.GATO));
        assertEquals(List.of("poliomavirus", "pacheco", "variola_aviaria"), codigos(EspecieVacina.LORO));
        assertEquals(codigos(EspecieVacina.LORO), codigos(EspecieVacina.CALOPSITA));
        assertTrue(codigos(EspecieVacina.HAMSTER).isEmpty());
        assertEquals(EspecieVacina.LORO, EspecieVacina.da("Papagaio"));
        assertEquals(EspecieVacina.CACHORRO, EspecieVacina.da("Cão"));
    }

    @Test
    void buscar_comUsuarioENome_retornaPetsDoTutor() {
        Pet rex = pet("Rex", "cachorro", 7L);
        when(petRepository.findByUsuarioIdAndNomeContainingIgnoreCase(7L, "rex")).thenReturn(List.of(rex));

        List<PetVacinaResumoDTO> pets = service.buscar(7L, " rex ");

        assertEquals(1, pets.size());
        assertEquals("Rex", pets.get(0).nome());
        assertEquals(7L, pets.get(0).usuarioId());
    }

    @Test
    void buscar_semNome_lancaRegraNegocio() {
        assertThrows(RegraNegocioException.class, () -> service.buscar(7L, "  "));
        verify(petRepository, never()).findByUsuarioIdAndNomeContainingIgnoreCase(any(), any());
    }

    @Test
    void detalhar_cachorro_trazVacinasDesmarcadas() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(pet("Rex", "cachorro", 7L)));
        when(vacinacaoRepository.findByPetId(1L)).thenReturn(List.of());

        PetVacinaDetalheDTO detalhe = service.detalhar(1L);

        assertEquals("Rex", detalhe.nome());
        assertEquals(5, detalhe.vacinas().size());
        assertEquals("V10 (múltipla canina)", detalhe.vacinas().get(0).nome());
        assertTrue(detalhe.vacinas().stream().noneMatch(VacinaStatusDTO::aplicada));
    }

    @Test
    void detalhar_hamster_naoTemVacinaDeRotina() {
        when(petRepository.findById(2L)).thenReturn(Optional.of(pet("Bolota", "hamster", 7L)));
        when(vacinacaoRepository.findByPetId(2L)).thenReturn(List.of());

        PetVacinaDetalheDTO detalhe = service.detalhar(2L);

        assertTrue(detalhe.vacinas().isEmpty());
        assertEquals("Hamsters não possuem vacinas de rotina obrigatórias.", detalhe.observacao());
    }

    @Test
    void salvar_marcaSomenteVacinasDaEspecieEDevolveOPet() {
        Pet pet = pet("Luna", "gato", 3L);
        pet.setRaca("Siamês");
        pet.setNascimento(LocalDate.of(2022, 4, 1));
        when(petRepository.findById(4L)).thenReturn(Optional.of(pet));
        when(vacinacaoRepository.findByPetId(4L)).thenReturn(List.of());
        when(vacinacaoRepository.save(any(Vacinacao.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        PetVacinaDetalheDTO salvo = service.salvar(4L, new SalvarVacinacaoRequest(List.of("antirrabica", "felv")));

        assertEquals("Luna", salvo.nome());
        assertEquals("Siamês", salvo.raca());
        assertEquals(LocalDate.of(2022, 4, 1), salvo.nascimento());
        assertEquals(3L, salvo.usuarioId());
        assertTrue(aplicada(salvo, "antirrabica"));
        assertTrue(aplicada(salvo, "felv"));
        assertFalse(aplicada(salvo, "v4"));
        assertFalse(aplicada(salvo, "giardia"));
    }

    @Test
    void salvar_comVacinaDeOutraEspecie_lancaRegraNegocio() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(pet("Rex", "cachorro", 7L)));

        assertThrows(RegraNegocioException.class,
                () -> service.salvar(1L, new SalvarVacinacaoRequest(List.of("felv"))));
        verify(vacinacaoRepository, never()).save(any());
    }

    @Test
    void detalhar_comPetInexistente_lancaNaoEncontrado() {
        when(petRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> service.detalhar(99L));
    }

    private List<String> codigos(EspecieVacina especie) {
        return VacinaCatalogo.porEspecie(especie).stream().map(VacinaCatalogo.Tipo::codigo).toList();
    }

    private boolean aplicada(PetVacinaDetalheDTO detalhe, String codigo) {
        return detalhe.vacinas().stream()
                .filter(vacina -> vacina.codigo().equals(codigo))
                .findFirst()
                .orElseThrow()
                .aplicada();
    }

    private Pet pet(String nome, String especie, Long usuarioId) {
        Pet pet = new Pet();
        pet.setId(nome.equals("Bolota") ? 2L : nome.equals("Luna") ? 4L : 1L);
        pet.setNome(nome);
        pet.setEspecie(especie);
        pet.setUsuarioId(usuarioId);
        return pet;
    }
}
