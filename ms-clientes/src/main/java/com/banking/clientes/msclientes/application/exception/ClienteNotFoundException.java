package com.banking.clientes.msclientes.application.exception;

import org.springframework.http.HttpStatus;

public class ClienteNotFoundException extends ApplicationException {

    public ClienteNotFoundException(Long id) {
        super("Cliente con id " + id + " no encontrado",
                "CLIENT_NOT_FOUND",
                HttpStatus.NOT_FOUND);
    }
}
