package com.petcare.vacina;

import com.petcare.common.exception.RecursoNaoEncontradoException;
import com.petcare.common.exception.RegraNegocioException;
import com.petcare.pet.Pet;
import com.petcare.pet.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VacinaService {

    private final PetRepository petRepository;
    private final VacinacaoRepository vacinacaoRepository;

    @Transactional(readOnly = true)
    public List<PetVacinaResumoDTO> buscar(Long usuarioId, String nome) {
        if (usuarioId == null) {
            throw new RegraNegocioException("Informe o id do usuário");
        }
        if (nome == null || nome.isBlank()) {
            throw new RegraNegocioException("Informe o nome do pet");
        }

        return petRepository.findByUsuarioIdAndNomeContainingIgnoreCase(usuarioId, nome.trim())
                .stream()
                .map(PetVacinaResumoDTO::de)
                .toList();
    }

    @Transactional(readOnly = true)
    public PetVacinaDetalheDTO detalhar(Long petId) {
        Pet pet = buscarPet(petId);
        EspecieVacina especie = EspecieVacina.da(pet.getEspecie());
        return montarDetalhe(pet, especie, vacinacaoRepository.findByPetId(pet.getId()));
    }

    @Transactional
    public PetVacinaDetalheDTO salvar(Long petId, SalvarVacinacaoRequest request) {
        Pet pet = buscarPet(petId);
        EspecieVacina especie = EspecieVacina.da(pet.getEspecie());
        List<VacinaCatalogo.Tipo> catalogo = VacinaCatalogo.porEspecie(especie);

        Set<String> marcadas = normalizarMarcadas(request.aplicadas());
        Set<String> permitidas = catalogo.stream()
                .map(VacinaCatalogo.Tipo::codigo)
                .collect(Collectors.toSet());

        if (!permitidas.containsAll(marcadas)) {
            throw new RegraNegocioException("Há vacina que não se aplica à espécie do pet");
        }

        Map<String, Vacinacao> atuais = vacinacaoRepository.findByPetId(pet.getId()).stream()
                .collect(Collectors.toMap(Vacinacao::getCodigo, Function.identity()));

        List<Vacinacao> persistidas = catalogo.stream()
                .map(tipo -> atualizar(pet, atuais.get(tipo.codigo()), tipo.codigo(), marcadas.contains(tipo.codigo())))
                .toList();

        return montarDetalhe(pet, especie, persistidas);
    }

    private Vacinacao atualizar(Pet pet, Vacinacao registro, String codigo, boolean aplicada) {
        if (registro == null) {
            registro = new Vacinacao();
            registro.setPet(pet);
            registro.setCodigo(codigo);
        }
        registro.setAplicada(aplicada);
        registro.setAtualizadoEm(LocalDate.now());
        return vacinacaoRepository.save(registro);
    }

    private Set<String> normalizarMarcadas(List<String> aplicadas) {
        Set<String> marcadas = new HashSet<>();
        for (String codigo : aplicadas) {
            marcadas.add(codigo.trim());
        }
        return marcadas;
    }

    private Pet buscarPet(Long petId) {
        return petRepository.findById(petId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pet não encontrado: " + petId));
    }

    private PetVacinaDetalheDTO montarDetalhe(Pet pet, EspecieVacina especie, List<Vacinacao> registros) {
        Map<String, Boolean> aplicadas = registros.stream()
                .collect(Collectors.toMap(Vacinacao::getCodigo, Vacinacao::isAplicada, (a, b) -> b));

        List<VacinaStatusDTO> vacinas = VacinaCatalogo.porEspecie(especie).stream()
                .map(tipo -> new VacinaStatusDTO(
                        tipo.codigo(),
                        tipo.nome(),
                        aplicadas.getOrDefault(tipo.codigo(), false)))
                .toList();

        return new PetVacinaDetalheDTO(
                pet.getId(),
                pet.getNome(),
                pet.getEspecie(),
                pet.getRaca(),
                pet.getNascimento(),
                pet.getUsuarioId(),
                VacinaCatalogo.observacao(especie),
                vacinas
        );
    }
}
