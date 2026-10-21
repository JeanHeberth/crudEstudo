package com.br.crudestudo.service;

import com.br.crudestudo.exception.ResourceNotFoundException;
import com.br.crudestudo.model.Usuario;
import com.br.crudestudo.model.request.UsuarioRequest;
import com.br.crudestudo.record.usuario.UsuarioResponse;
import com.br.crudestudo.repository.UsuarioRepository;
import com.br.crudestudo.service.validation.usuario.ValidadorCadastroUsuario;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final List<ValidadorCadastroUsuario> validadores;

    public UsuarioResponse salvarUsuario(UsuarioRequest usuarioRequest) {

        validadores.forEach(v ->
                v.validarCadastroUsuario(usuarioRequest, null));

        Usuario usuario = new Usuario();
        usuario.setNome(usuarioRequest.getNome());
        usuario.setEmail(usuarioRequest.getEmail());
        usuario.setCpf(usuarioRequest.getCpfLimpo());
        return UsuarioResponse.deEntidade(usuarioRepository.save(usuario));

    }

    public Page<UsuarioResponse> buscarTodosUsuarios(Pageable paginacao) {
        return usuarioRepository.findAll(paginacao)
                .map(UsuarioResponse::deEntidade);
    }

    public UsuarioResponse buscarUsuario(Long id) {
        return usuarioRepository.findById(id)
                .map(UsuarioResponse::deEntidade)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário nao encontrado com o id: " + id));
    }

    public UsuarioResponse atualizarUsuario(@Valid Long id, UsuarioRequest usuarioRequest) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o ID: " + id));

        validadores.forEach(validador -> validador.validarCadastroUsuario(usuarioRequest, id));

        usuarioExistente.setNome(usuarioRequest.getNome());
        usuarioExistente.setEmail(usuarioRequest.getEmail());
        usuarioExistente.setCpf(usuarioRequest.getCpfLimpo());

        Usuario usuarioAtualizado = usuarioRepository.save(usuarioExistente);
        return UsuarioResponse.deEntidade(usuarioAtualizado);

    }
}


