package com.br.crudestudo.exception;

public class CpfDuplicadoException extends ConflitoException {
    public CpfDuplicadoException(String cpfJaCadastrado) {
        super(cpfJaCadastrado);
    }
}
