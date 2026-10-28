package com.br.crudestudo.service.validation.usuario;

import com.br.crudestudo.record.usuario.request.UsuarioRequest;

public interface ValidadorCadastroUsuario {
        void validarCadastroUsuario(UsuarioRequest usuarioRequest, Long id);
    }
