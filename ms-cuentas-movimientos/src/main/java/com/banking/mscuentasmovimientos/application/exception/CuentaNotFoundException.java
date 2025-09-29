package com.banking.mscuentasmovimientos.application.exception;

public class CuentaNotFoundException extends ApplicationException{

    public CuentaNotFoundException(Long id) {
        super("Cuenta con id " + id + " no encontrada",
                "CUENTA_NOT_FOUND",
                org.springframework.http.HttpStatus.NOT_FOUND);
    }
}
