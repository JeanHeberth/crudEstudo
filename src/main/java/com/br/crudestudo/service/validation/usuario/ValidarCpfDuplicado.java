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
    public void validarCadastroUsuario(UsuarioRequest usuarioRequest, Long id) {
        if (id == null) {
            if (usuarioRepository.existsByCpf(usuarioRequest.getCpfLimpo())) {
                throw new CpfDuplicadoException("CPF já cadastrado");
            }
        } else {
            if (usuarioRepository.existsByCpfAndIdNot(usuarioRequest.getCpfLimpo(), id)) {
                throw new CpfDuplicadoException("CPF já cadastrado por outro usuário");
            }
        }
    }
}
