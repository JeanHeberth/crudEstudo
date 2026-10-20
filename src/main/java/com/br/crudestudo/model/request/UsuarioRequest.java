package com.br.crudestudo.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UsuarioRequest {

    @NotBlank(message = "O nome do usuário é obrigatório")
    private String nome;

    @NotBlank(message = "O email do usuário é obrigatório")
    @Email(message = "Por favor, insira um formato de e-mail válido (ex: usuario@email.com)") // Adicione esta linha!
    private String email;

    @NotBlank(message = "O cpf é obrigatório")
    private String cpf;
}
