package com.banking.mscuentasmovimientos.application.exception;

import org.springframework.http.HttpStatus;

public class SaldoNoDisponibleException extends ApplicationException{

    public SaldoNoDisponibleException(String numeroCuenta) {
        super("Saldo no disponible en la cuenta " + numeroCuenta,
                "INSUFFICIENT_FUNDS",
                HttpStatus.BAD_REQUEST);
    }
}
