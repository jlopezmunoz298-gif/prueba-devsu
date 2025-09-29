package com.banking.mscuentasmovimientos.domain.entity;

import com.banking.mscuentasmovimientos.domain.enums.TipoMovimiento;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class Movimiento {
    private Long id;
    private LocalDateTime fecha;
    private TipoMovimiento tipo;
    private BigDecimal valor;
    private BigDecimal saldo;
    private Long cuentaId;
}
