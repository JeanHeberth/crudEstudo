package com.br.crudestudo.service;

import com.br.crudestudo.exception.ResourceNotFoundException;
import com.br.crudestudo.model.Usuario;
import com.br.crudestudo.model.request.UsuarioRequest;
import com.br.crudestudo.record.usuario.UsuarioResponse;
import com.br.crudestudo.repository.UsuarioRepository;
import com.br.crudestudo.service.validation.usuario.ValidadorCadastroUsuario;
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
                v.validarCadastroUsuario(usuarioRequest));

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
}
