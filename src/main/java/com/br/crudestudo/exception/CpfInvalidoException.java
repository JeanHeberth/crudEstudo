package com.br.crudestudo.exception;

public class CpfInvalidoException extends RegraNegocioException {
    public CpfInvalidoException(String mensagem) {
        super(mensagem);
    }
}
