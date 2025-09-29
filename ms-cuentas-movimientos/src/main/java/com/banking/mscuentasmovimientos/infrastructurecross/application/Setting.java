package com.banking.mscuentasmovimientos.infrastructurecross.application;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class Setting {

    @Value("${spring.ms-clientes.usuarios.url}")
    private String msClientes;
}
