package com.banking.clientes.msclientes.application.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@Getter
public class ApplicationException extends RuntimeException implements Serializable {
    private static final long serialVersionUID = 1L;

    private final HttpStatus httpStatus;
    private final String codigoError;

    public ApplicationException(String mensaje, String codigoError, HttpStatus httpStatus) {
        super(mensaje);
        this.codigoError = codigoError;
        this.httpStatus = httpStatus;
    }

    public ApplicationException(String mensaje, String codigoError, HttpStatus httpStatus, Throwable cause) {
        super(mensaje, cause);
        this.codigoError = codigoError;
        this.httpStatus = httpStatus;
    }
}
