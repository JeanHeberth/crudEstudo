package com.br.crudestudo.exception;

public class EmailDuplicadoException extends ConflitoException {
    public EmailDuplicadoException(String emailJaCadastrado) {
        super(emailJaCadastrado);
    }
}
