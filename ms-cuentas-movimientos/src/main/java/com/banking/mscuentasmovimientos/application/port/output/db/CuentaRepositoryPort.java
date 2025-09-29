package com.banking.mscuentasmovimientos.application.port.output.db;

import com.banking.mscuentasmovimientos.domain.entity.Cuenta;

import java.util.List;
import java.util.Optional;

public interface CuentaRepositoryPort {
    Cuenta save(Cuenta cuenta);
    Optional<Cuenta> findById(Long id);
    Optional<Cuenta> findByNumeroCuenta(String numeroCuenta);
    List<Cuenta> findAll();
    void deleteById(Long id);
}
