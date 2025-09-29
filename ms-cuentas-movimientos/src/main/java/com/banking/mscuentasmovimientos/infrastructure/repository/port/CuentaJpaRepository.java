package com.banking.mscuentasmovimientos.infrastructure.repository.port;

import com.banking.mscuentasmovimientos.infrastructure.repository.entity.CuentaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CuentaJpaRepository extends JpaRepository<CuentaEntity, Long> {
    Optional<CuentaEntity> findByNumeroCuenta(String numeroCuenta);
}
