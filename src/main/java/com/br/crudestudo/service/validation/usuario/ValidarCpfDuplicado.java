package com.br.crudestudo.service.validation.usuario;

import com.br.crudestudo.exception.CpfDuplicadoException;
import com.br.crudestudo.model.request.UsuarioRequest;
import com.br.crudestudo.repository.UsuarioRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ValidarCpfDuplicado implements ValidadorCadastroUsuario {

    private final UsuarioRepository usuarioRepository;

    @Override
    public void validarCadastroUsuario(UsuarioRequest usuarioRequest) {
        if (usuarioRepository.existsByCpf(usuarioRequest.getCpfLimpo())) {
            throw new CpfDuplicadoException("Cpf duplicado");
        }
    }
}
