package com.banking.clientes.msclientes.application.exception;

public class ClienteAlreadyExistsException extends ApplicationException {
    public ClienteAlreadyExistsException(String identificacion) {
        super("Cliente con identificacion " + identificacion + " ya existe",
                "CLIENT_ALREADY_EXISTS",
                org.springframework.http.HttpStatus.CONFLICT);
    }
}
