package com.br.crudestudo.model.request;

import lombok.Data;

@Data
public class UsuarioRequest {
    private String nome;
    private String email;
    private String cpf;
}
