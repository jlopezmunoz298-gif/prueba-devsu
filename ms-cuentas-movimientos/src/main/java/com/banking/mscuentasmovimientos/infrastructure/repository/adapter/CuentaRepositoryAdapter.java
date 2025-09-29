package com.banking.mscuentasmovimientos.infrastructure.repository.adapter;

import com.banking.mscuentasmovimientos.application.port.output.db.CuentaRepositoryPort;
import com.banking.mscuentasmovimientos.domain.entity.Cuenta;
import com.banking.mscuentasmovimientos.infrastructure.repository.entity.CuentaEntity;
import com.banking.mscuentasmovimientos.infrastructure.repository.port.CuentaJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CuentaRepositoryAdapter implements CuentaRepositoryPort {

    private final CuentaJpaRepository jpa;

    @Override
    public Cuenta save(Cuenta cuenta) {
        return toDomain(jpa.save(toEntity(cuenta)));
    }

    @Override
    public Optional<Cuenta> findById(Long id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Cuenta> findByNumeroCuenta(String numeroCuenta) {
        return jpa.findByNumeroCuenta(numeroCuenta).map(this::toDomain);
    }

    @Override
    public List<Cuenta> findAll() {
        return jpa.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        jpa.deleteById(id);
    }

    // --------- Mapping ----------
    private CuentaEntity toEntity(Cuenta c) {
        if (c == null) return null;
        return CuentaEntity.builder()
                .id(c.getId())
                .numeroCuenta(c.getNumeroCuenta())
                .tipoCuenta(c.getTipoCuenta())
                .saldoInicial(c.getSaldoInicial())
                .estado(c.getEstado())
                .clienteId(c.getClienteId())
                .build();
    }

    private Cuenta toDomain(CuentaEntity e) {
        if (e == null) return null;
        return Cuenta.builder()
                .id(e.getId())
                .numeroCuenta(e.getNumeroCuenta())
                .tipoCuenta(e.getTipoCuenta())
                .saldoInicial(e.getSaldoInicial())
                .estado(e.getEstado())
                .clienteId(e.getClienteId())
                .build();
    }
}
