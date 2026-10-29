package com.br.crudestudo.factory;

import com.br.crudestudo.model.Usuario;
import com.br.crudestudo.record.usuario.request.UsuarioRequest;
import com.br.crudestudo.record.usuario.request.UsuarioUpdateRequest;

public class UsuarioFactory {
    private UsuarioFactory() {
    }

    public static UsuarioRequest usuarioParaCadastrar() {
        return new UsuarioRequest(
                "Maria da Silva",
                "maria@email.com",
                "00000000191"
        );
    }

    public static UsuarioRequest usuarioParaCadastrar(
            String nome,
            String email,
            String cpf
    ){
        return new UsuarioRequest(nome, email, cpf);
    }

    public static UsuarioRequest usuarioParaCadastrar(String cpf) {
        return new UsuarioRequest(
                "Maria da Silva",
                "maria@email.com",
                cpf
        );
    }

    public static Usuario usuarioExistente() {
        return new Usuario(
                99L,
                "Maria",
                "maria@email.com",
                "00000000019"
        );
    }

    public static Usuario usuarioExistente(
            Long id,
            String nome,
            String email,
            String cpf

    ) {
        return new Usuario(id, nome, email, cpf);
    }

    public static UsuarioUpdateRequest usuarioParaAtualizar() {
        return new UsuarioUpdateRequest(
                "Maria Atualizado",
                "maria@email.com"
        );
    }

    public static UsuarioUpdateRequest usuarioParaAtualizar(
            String nome,
            String email
    ) {
        return new UsuarioUpdateRequest(nome, email);
    }
}
