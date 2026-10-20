package com.br.crudestudo.exception;

import com.br.crudestudo.exception.record.ErrorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Segurança para JSON malformatado 400
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorMessage> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        ErrorMessage errorMessage = new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                "O corpo da requisição JSON está malformatado ou inválido."
        );
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    // Segurança para servidor fora 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleGeneralException(Exception ex) {
        if (ex instanceof ErrorResponse springError) {
            int status = springError.getStatusCode().value();
            String detalhe = springError.getBody().getDetail();

            if (detalhe == null) {
                detalhe = ex.getMessage();
            }

            return ResponseEntity.status(status).body(new ErrorMessage(status, detalhe));
        }

        log.error("Ocorreu um erro interno no servidor", ex);

        return ResponseEntity.internalServerError().body(
                new ErrorMessage(500, "Ocorreu um erro interno no servidor. Por favor, tente novamente mais tarde."));
    }

    // Segurança para dados inválidos 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, List<String>> errosDosCampos = new HashMap<>();

        for (FieldError fieldError : ex.getFieldErrors()) {
            String campo = fieldError.getField();
            String mensagem = fieldError.getDefaultMessage();
            errosDosCampos.computeIfAbsent(campo, k -> new ArrayList<>()).add(mensagem);
        }

        ErrorMessage errorMessage = new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                "Falha na validação dos dados enviados",
                errosDosCampos
        );
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    // Segurança para dados não encontrados 404
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorMessage> resourceNotFound(ResourceNotFoundException ex) {
        ErrorMessage error = new ErrorMessage(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }


    // Segurança para dados duplicados 409
    @ExceptionHandler(ConflitoException.class)
    public ResponseEntity<ErrorMessage> handleConflito(ConflitoException ex) {
        ErrorMessage errorMessage = new ErrorMessage(
                HttpStatus.CONFLICT.value(),
                ex.getMessage()
        );
        return new ResponseEntity<>(errorMessage, HttpStatus.CONFLICT);
    }

    // Segurança para dados já cadastrados 422
    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErrorMessage> handleRegraNegocio(RegraNegocioException ex) {
        ErrorMessage error = new ErrorMessage(
                HttpStatus.UNPROCESSABLE_CONTENT.value(),
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.UNPROCESSABLE_CONTENT);
    }
}
