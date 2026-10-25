package com.br.crudestudo.record;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Resposta padrão de erro")
public record ErrorMessage (
        @Schema(description = "Código HTTP", example = "400") int status,
        @Schema(description = "Descrição do erro", example = "Falha na validação dos dados enviados") String mensagem,
        @Schema(description = "Erros por campo (apenas em falhas de validação)") Map<String, List<String>> errors) {
    public  ErrorMessage (int status, String mensagem) {
        this(status, mensagem, null);
    }
}
