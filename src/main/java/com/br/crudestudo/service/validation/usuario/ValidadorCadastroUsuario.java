package com.br.crudestudo.service.validation.usuario;

import com.br.crudestudo.model.request.UsuarioRequest;

public interface ValidadorCadastroUsuario {
    void validarCadastroUsuario(UsuarioRequest usuarioRequest, Long id);
}
