package com.banking.mscuentasmovimientos.application;

import com.banking.mscuentasmovimientos.application.dto.ApiResponse;
import com.banking.mscuentasmovimientos.application.dto.MovimientoDTO;
import com.banking.mscuentasmovimientos.application.exception.CuentaInactivaException;
import com.banking.mscuentasmovimientos.application.exception.CuentaNotFoundException;
import com.banking.mscuentasmovimientos.application.exception.SaldoNoDisponibleException;
import com.banking.mscuentasmovimientos.application.port.interactor.IMovimientoService;
import com.banking.mscuentasmovimientos.application.port.output.db.CuentaRepositoryPort;
import com.banking.mscuentasmovimientos.application.port.output.db.MovimientoRespositoryPort;
import com.banking.mscuentasmovimientos.domain.entity.Cuenta;
import com.banking.mscuentasmovimientos.domain.entity.Movimiento;
import com.banking.mscuentasmovimientos.domain.enums.TipoMovimiento;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovimientoServiceImpl implements IMovimientoService {

    private final MovimientoRespositoryPort movimientoRepo;
    private final CuentaRepositoryPort cuentaRepo;

    @Override
    public ApiResponse<MovimientoDTO> registrarMovimiento(MovimientoDTO dto) {
        Cuenta cuenta = cuentaRepo.findById(dto.getCuentaId())
                .orElseThrow(() -> new CuentaNotFoundException(dto.getCuentaId()));

        if (!cuenta.getEstado()) {
            throw new CuentaInactivaException(dto.getCuentaId());
        }

        TipoMovimiento tipoMovimiento = TipoMovimiento.valueOf(dto.getTipo().toUpperCase());
        BigDecimal nuevoSaldo = calcularNuevoSaldo(cuenta.getSaldoInicial(), dto.getValor(), tipoMovimiento);

        if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new SaldoNoDisponibleException(cuenta.getNumeroCuenta());
        }

        cuenta.setSaldoInicial(nuevoSaldo);
        cuentaRepo.save(cuenta);

        Movimiento movimiento = buildMovimiento(dto, cuenta, tipoMovimiento, nuevoSaldo);
        Movimiento saved = movimientoRepo.save(movimiento);

        return new ApiResponse<>("Movimiento registrado correctamente", toDTO(saved, cuenta));
    }


    @Override
    public List<ApiResponse<MovimientoDTO>> listarPorCuentaId(Long cuentaId) {
        return movimientoRepo.findByCuentaId(cuentaId).stream()
                .map(m -> new ApiResponse<>("Movimiento encontrado",
                        toDTO(m, cuentaRepo.findById(m.getCuentaId()).orElse(null))))
                .toList();
    }

    @Override
    public ApiResponse<List<MovimientoDTO>> listarTodos() {
        List<MovimientoDTO> movimientos = movimientoRepo.findAll().stream()
                .map(m -> toDTO(m, cuentaRepo.findById(m.getCuentaId()).orElse(null)))
                .toList();
        return new ApiResponse<>("Listado de movimientos obtenido correctamente", movimientos);
    }

    @Override
    public List<ApiResponse<MovimientoDTO>> obtenerReporte(Long clienteId, LocalDate desde, LocalDate hasta) {
        List<Movimiento> movimientos = movimientoRepo.findByClienteAndFechaBetween(clienteId, desde, hasta);

        return movimientos.stream()
                .map(m -> {
                    Cuenta cuenta = cuentaRepo.findById(m.getCuentaId()).orElse(null);
                    return new ApiResponse<>("Movimiento en rango de fechas", toDTO(m, cuenta));
                })
                .toList();
    }

    // --------- Helpers ----------
    private BigDecimal calcularNuevoSaldo(BigDecimal saldoActual, BigDecimal valor, TipoMovimiento tipo) {
        return tipo == TipoMovimiento.RETIRO
                ? saldoActual.subtract(valor)
                : saldoActual.add(valor);
    }

    private Movimiento buildMovimiento(MovimientoDTO dto, Cuenta cuenta, TipoMovimiento tipo, BigDecimal nuevoSaldo) {
        return Movimiento.builder()
                .fecha(LocalDateTime.now())
                .tipo(tipo)
                .valor(dto.getValor())
                .saldo(nuevoSaldo)
                .cuentaId(cuenta.getId())
                .build();
    }

    private MovimientoDTO toDTO(Movimiento m, Cuenta cuenta) {
        return MovimientoDTO.builder()
                .id(m.getId())
                .fecha(m.getFecha())
                .tipo(m.getTipo().name())
                .valor(m.getValor())
                .saldo(m.getSaldo())
                .cuentaId(m.getCuentaId())
                .numeroCuenta(cuenta != null ? cuenta.getNumeroCuenta() : null)
                .tipoCuenta(cuenta != null ? cuenta.getTipoCuenta() : null)
                .saldoInicial(cuenta != null ? cuenta.getSaldoInicial() : null)
                .estado(cuenta != null ? cuenta.getEstado() : null)
                .build();
    }
}
