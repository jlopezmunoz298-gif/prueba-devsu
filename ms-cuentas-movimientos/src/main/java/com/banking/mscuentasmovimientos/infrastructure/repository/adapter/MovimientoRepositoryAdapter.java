package com.banking.mscuentasmovimientos.infrastructure.repository.adapter;

import com.banking.mscuentasmovimientos.application.port.output.db.MovimientoRespositoryPort;
import com.banking.mscuentasmovimientos.domain.entity.Movimiento;
import com.banking.mscuentasmovimientos.domain.enums.TipoMovimiento;
import com.banking.mscuentasmovimientos.infrastructure.repository.entity.CuentaEntity;
import com.banking.mscuentasmovimientos.infrastructure.repository.entity.MovimientoEntity;
import com.banking.mscuentasmovimientos.infrastructure.repository.port.MovimientoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MovimientoRepositoryAdapter implements MovimientoRespositoryPort {

    private final MovimientoJpaRepository jpa;

    @Override
    public Movimiento save(Movimiento movimiento) {
        MovimientoEntity entity = MovimientoEntity.builder()
                .id(movimiento.getId())
                .fecha(movimiento.getFecha())
                .tipoMovimiento(movimiento.getTipo().name())
                .valor(movimiento.getValor())
                .saldo(movimiento.getSaldo())
                .cuenta(CuentaEntity.builder().id(movimiento.getCuentaId()).build())
                .build();

        MovimientoEntity saved = jpa.save(entity);

        return Movimiento.builder()
                .id(saved.getId())
                .fecha(saved.getFecha())
                .tipo(TipoMovimiento.valueOf(saved.getTipoMovimiento()))
                .valor(saved.getValor())
                .saldo(saved.getSaldo())
                .cuentaId(saved.getCuenta().getId())
                .build();
    }


    @Override
    public Optional<Movimiento> findById(Long id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public List<Movimiento> findByCuentaId(Long cuentaId) {
        return jpa.findByCuentaId(cuentaId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Movimiento> findAll() {
        return jpa.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public List<Movimiento> findByClienteAndFechaBetween(Long clienteId, LocalDate desde, LocalDate hasta) {
        return jpa.findByCuentaClienteIdAndFechaBetween(clienteId, desde.atStartOfDay(), hasta.atTime(23, 59, 59))
                .stream()
                .map(this::toDomain)
                .toList();
    }

    // --------- Mapping ----------
    private MovimientoEntity toEntity(Movimiento m) {
        if (m == null) return null;
        return MovimientoEntity.builder()
                .id(m.getId())
                .fecha(m.getFecha())
                .tipoMovimiento(String.valueOf(m.getTipo()))
                .valor(m.getValor())
                .saldo(m.getSaldo())
                .cuenta(null)
                .build();
    }

    private Movimiento toDomain(MovimientoEntity e) {
        if (e == null) return null;
        return Movimiento.builder()
                .id(e.getId())
                .fecha(e.getFecha())
                .tipo(TipoMovimiento.valueOf(e.getTipoMovimiento()))
                .valor(e.getValor())
                .saldo(e.getSaldo())
                .cuentaId(e.getCuenta().getId())
                .build();
    }
}
