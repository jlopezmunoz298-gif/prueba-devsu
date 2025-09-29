package com.banking.mscuentasmovimientos.application.exception;

import org.springframework.http.HttpStatus;

public class CuentaInactivaException extends ApplicationException {

    public CuentaInactivaException(Long id) {
        super("La cuenta con id " + id + " se encuentra eliminada o inactiva",
                "CUENTA_INACTIVA",
                HttpStatus.BAD_REQUEST);
    }
}
