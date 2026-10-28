package com.br.crudestudo.record.usuario.response;

import com.br.crudestudo.model.Usuario;
import io.swagger.v3.oas.annotations.media.Schema;


public record UsuarioResponse(

     @Schema(description = "Identificador do usuário", example = "1") Long id,
     @Schema(description = "Nome completo", example = "Maria da Silva") String nome,
     @Schema(description = "E-mail do usuário", example = "maria@email.com") String email,
     @Schema(description = "CPF somente com números", example = "12345678909") String cpf
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
