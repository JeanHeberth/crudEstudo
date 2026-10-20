package com.br.crudestudo.service.validation.usuario;

import com.br.crudestudo.exception.CpfInvalidoException;
import com.br.crudestudo.model.request.UsuarioRequest;
import org.springframework.stereotype.Component;

@Component
public class ValidarCpfInvalido implements ValidadorCadastroUsuario {

    @Override
    public void validarCadastroUsuario(UsuarioRequest usuarioRequest) {
        if (usuarioRequest.getCpfLimpo().matches("(\\d)\\1{10}")) {
            throw new CpfInvalidoException("Cpf invalido");
        }
    }
}