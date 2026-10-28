package com.br.crudestudo.record.usuario.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UsuarioUpdateRequest (
        @NotBlank(message = "O nome é obrigatório.")
        @Size(min = 3, max = 100, message = "O nome deve conter entre 3 e 100 caracteres.")
        String nome,

        @NotBlank(message = "O e-mail é obrigatório.")
        @Pattern(regexp = "^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$", message = "Por favor, insira um formato de e-mail válido.")
        String email
){}
