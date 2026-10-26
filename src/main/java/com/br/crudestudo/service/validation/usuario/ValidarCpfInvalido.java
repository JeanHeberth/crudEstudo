package com.br.crudestudo.service.validation.usuario;

import com.br.crudestudo.exception.CpfInvalidoException;
import com.br.crudestudo.record.usuario.request.UsuarioRequest;
import org.springframework.stereotype.Component;

@Component
public class ValidarCpfInvalido implements ValidadorCadastroUsuario {

    @Override
    public void validarCadastroUsuario(UsuarioRequest usuarioRequest, Long id) {
        if (usuarioRequest.getCpfLimpo().matches("(\\d)\\1{10}")) {
            throw new CpfInvalidoException("Cpf invalido");
        }
    }
}