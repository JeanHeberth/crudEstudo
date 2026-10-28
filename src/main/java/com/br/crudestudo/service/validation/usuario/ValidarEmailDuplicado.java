package com.br.crudestudo.service.validation.usuario;

import com.br.crudestudo.exception.EmailDuplicadoException;
import com.br.crudestudo.record.usuario.request.UsuarioRequest;
import com.br.crudestudo.repository.UsuarioRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ValidarEmailDuplicado implements ValidadorCadastroUsuario {

    private final UsuarioRepository usuarioRepository;

    @Override
    public void validarCadastroUsuario(UsuarioRequest usuarioRequest, Long id) {
        if (id == null) {
            if (usuarioRepository.existsByEmail(usuarioRequest.email())) {
                throw new EmailDuplicadoException("Email já cadastrado");
            }
        } else {
            if (usuarioRepository.existsByEmailAndIdNot(usuarioRequest.email(), id)) {
                throw new EmailDuplicadoException("Email já cadastrado por outro usuário");
            }
        }
    }
}