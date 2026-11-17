package com.petcare.usuario;

import com.petcare.common.exception.RecursoNaoEncontradoException;
import com.petcare.common.exception.RegraNegocioException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder encoder;

    @Transactional
    public UsuarioResponseDTO cadastrar(UsuarioRequestDTO dto) {
        String email = normalizar(dto.email());
        if (repository.existsByEmail(email)) {
            throw new RegraNegocioException("Já existe um usuário com esse e-mail");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome().trim());
        usuario.setEmail(email);
        usuario.setSenha(encoder.encode(dto.senha()));
        return UsuarioResponseDTO.de(repository.save(usuario));
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO login(LoginRequestDTO dto) {
        return repository.findByEmail(normalizar(dto.email()))
                .filter(u -> encoder.matches(dto.senha(), u.getSenha()))
                .map(UsuarioResponseDTO::de)
                .orElseThrow(() -> new RegraNegocioException("E-mail ou senha inválidos"));
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(Long id) {
        return UsuarioResponseDTO.de(buscar(id));
    }

    @Transactional
    public UsuarioResponseDTO atualizar(Long id, UsuarioAtualizacaoDTO dto) {
        Usuario usuario = buscar(id);
        String email = normalizar(dto.email());

        repository.findByEmail(email)
                .filter(outro -> !outro.getId().equals(id))
                .ifPresent(outro -> {
                    throw new RegraNegocioException("Já existe um usuário com esse e-mail");
                });

        usuario.setNome(dto.nome().trim());
        usuario.setEmail(email);
        return UsuarioResponseDTO.de(usuario);
    }

    private Usuario buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado: " + id));
    }

    private String normalizar(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
