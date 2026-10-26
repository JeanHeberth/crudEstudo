package com.br.crudestudo.service;

import com.br.crudestudo.exception.ResourceNotFoundException;
import com.br.crudestudo.model.Usuario;
import com.br.crudestudo.record.usuario.request.UsuarioRequest;
import com.br.crudestudo.record.usuario.request.UsuarioUpdateRequest;
import com.br.crudestudo.record.usuario.response.UsuarioResponse;
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
        usuario.setNome(usuarioRequest.nome());
        usuario.setEmail(usuarioRequest.email());
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

    public UsuarioResponse atualizarUsuario(Long id, @Valid UsuarioUpdateRequest usuarioUpdateRequest) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o ID: " + id));

        String emailNormalizado = usuarioUpdateRequest.email()
                .trim()
                .toLowerCase();

        // apenas com o e-mail que queremos validar, passando o ID atual para evitar o falso 409!
        UsuarioRequest dadosParaValidar = new UsuarioRequest(
                usuarioUpdateRequest.nome(),
                emailNormalizado,
                usuarioExistente.getCpf());

        validadores.forEach(v -> v.validarCadastroUsuario(dadosParaValidar, id));

        usuarioExistente.setNome(usuarioUpdateRequest.nome().trim());
        usuarioExistente.setEmail(emailNormalizado);

        Usuario usuarioAtualizado = usuarioRepository.save(usuarioExistente);
        return UsuarioResponse.deEntidade(usuarioAtualizado);

    }
}


