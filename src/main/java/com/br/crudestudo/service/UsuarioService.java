package com.br.crudestudo.service;

import com.br.crudestudo.exception.ResourceNotFoundException;
import com.br.crudestudo.model.Usuario;
import com.br.crudestudo.model.request.UsuarioRequest;
import com.br.crudestudo.model.response.UsuarioResponse;
import com.br.crudestudo.repository.UsuarioRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UsuarioService {

    private UsuarioRepository usuarioRepository;

    public UsuarioResponse salvarUsuario(UsuarioRequest usuarioRequest) {

        if (usuarioRequest.getEmail() == null || usuarioRequest.getEmail().isEmpty()) {
            throw new ResourceNotFoundException("Email nao digitado");
        }
        if (usuarioRepository.existsByEmail(usuarioRequest.getEmail())) {
            throw new ResourceNotFoundException("Email já cadastrado");
        }
        if (usuarioRepository.existsByCpf(usuarioRequest.getCpf())) {
            throw new ResourceNotFoundException("CPF já cadastrado");
        }


        Usuario usuario = new Usuario();
        usuario.setNome(usuarioRequest.getNome());
        usuario.setEmail(usuarioRequest.getEmail());
        usuario.setCpf(usuarioRequest.getCpf());
        return UsuarioResponse.deEntidade(usuarioRepository.save(usuario));

    }
}
