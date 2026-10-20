package com.br.crudestudo.record;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorMessage (int status, String mensagem, Map<String, List<String>> errors) {
    public  ErrorMessage (int status, String mensagem) {
        this(status, mensagem, null);
    }
}
