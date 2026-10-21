package com.br.crudestudo.record.usuario;

import com.br.crudestudo.model.Usuario;


public record UsuarioResponse(

     Long id,
     String nome,
     String email,
     String cpf
){

    public static UsuarioResponse deEntidade(Usuario usuarioSalvo) {
        return new UsuarioResponse(
                usuarioSalvo.getId(),
                usuarioSalvo.getNome(),
                usuarioSalvo.getEmail(),
                usuarioSalvo.getCpf()
        );
    }
}
