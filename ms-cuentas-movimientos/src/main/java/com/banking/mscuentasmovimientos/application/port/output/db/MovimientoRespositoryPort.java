package com.banking.mscuentasmovimientos.application.port.output.db;

import com.banking.mscuentasmovimientos.domain.entity.Movimiento;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MovimientoRespositoryPort {
    Movimiento save(Movimiento movimiento);
    Optional<Movimiento> findById(Long id);
    List<Movimiento> findByCuentaId(Long cuentaId);
    List<Movimiento> findAll();
    List<Movimiento> findByClienteAndFechaBetween(Long clienteId, LocalDate desde, LocalDate hasta);


}
