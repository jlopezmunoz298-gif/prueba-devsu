package com.banking.clientes.msclientes.application.port.output.db;

import com.banking.clientes.msclientes.domain.entity.Cliente;

import java.util.List;
import java.util.Optional;

public interface ClienteRepositoryPort {

    Cliente save(Cliente cliente);
    Optional<Cliente> findById(Long id);
    Optional<Cliente> findByIdentificacion(String identificacion);
    List<Cliente> findAll();
    void deleteById(Long id);
}
