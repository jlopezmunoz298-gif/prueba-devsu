package com.banking.mscuentasmovimientos.infrastructure.repository.port;

import com.banking.mscuentasmovimientos.infrastructure.repository.entity.MovimientoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MovimientoJpaRepository extends JpaRepository<MovimientoEntity, Long> {
    List<MovimientoEntity> findByCuentaId(Long cuentaId);
    List<MovimientoEntity> findByCuentaClienteIdAndFechaBetween(
            Long clienteId,
            LocalDateTime desde,
            LocalDateTime hasta
    );
}
