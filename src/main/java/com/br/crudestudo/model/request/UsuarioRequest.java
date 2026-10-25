package com.br.crudestudo.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UsuarioRequest {

    @Schema(description = "Nome completo (3 a 50 caracteres)", example = "Maria da Silva")
    @NotBlank(message = "O nome do usuário é obrigatório")
    @Size(min = 3, max = 50, message = "O nome deve ter entre 3 e 50 caracteres")
    private String nome;

    @Schema(description = "E-mail único por usuário", example = "maria@email.com")
    @NotBlank(message = "O email do usuário é obrigatório")
    @Pattern(regexp = "^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$", message = "Por favor, insira um formato de e-mail válido (ex: usuario@email.com)")
    private String email;

    @Schema(description = "CPF válido e único, com 11 dígitos ou formatado", example = "123.456.789-09")
    @NotBlank(message = "O CPF é obrigatório")
    @Pattern(
            regexp = "^(\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2})$",
            message = "Por favor, insira o CPF no formato válido"
    )
    private String cpf;

    public String getCpfLimpo() {
        return cpf == null ? null : cpf.replaceAll("[^0-9]", "");
    }
}
