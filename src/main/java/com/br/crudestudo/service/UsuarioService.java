package com.br.crudestudo.service;

import com.br.crudestudo.model.Usuario;
import com.br.crudestudo.model.request.UsuarioRequest;
import com.br.crudestudo.model.response.UsuarioResponse;
import com.br.crudestudo.repository.UsuarioRepository;
import com.br.crudestudo.service.validation.usuario.ValidadorCadastroUsuario;
import lombok.AllArgsConstructor;
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
}
