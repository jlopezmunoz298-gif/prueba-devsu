package com.banking.mscuentasmovimientos.infrastructure.agent.cliente.exception;

import com.banking.mscuentasmovimientos.application.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class ClienteNotFoundException extends ApplicationException {

    public ClienteNotFoundException(String id) {
        super("Cliente con id " + id + " no encontrado",
                "CLIENT_NOT_FOUND",
                HttpStatus.NOT_FOUND);
    }
}
