package com.banking.mscuentasmovimientos.unit;

import com.banking.mscuentasmovimientos.application.MovimientoServiceImpl;
import com.banking.mscuentasmovimientos.application.dto.ApiResponse;
import com.banking.mscuentasmovimientos.application.dto.MovimientoDTO;
import com.banking.mscuentasmovimientos.application.exception.CuentaInactivaException;
import com.banking.mscuentasmovimientos.application.exception.CuentaNotFoundException;
import com.banking.mscuentasmovimientos.application.exception.SaldoNoDisponibleException;
import com.banking.mscuentasmovimientos.application.port.output.db.CuentaRepositoryPort;
import com.banking.mscuentasmovimientos.application.port.output.db.MovimientoRespositoryPort;
import com.banking.mscuentasmovimientos.domain.entity.Cuenta;
import com.banking.mscuentasmovimientos.domain.entity.Movimiento;
import com.banking.mscuentasmovimientos.domain.enums.TipoMovimiento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
public class MovimientoServiceImplTests {
    private MovimientoRespositoryPort movimientoRepo;
    private CuentaRepositoryPort cuentaRepo;
    private MovimientoServiceImpl service;

    @BeforeEach
    void setUp() {
        movimientoRepo = mock(MovimientoRespositoryPort.class);
        cuentaRepo = mock(CuentaRepositoryPort.class);
        service = new MovimientoServiceImpl(movimientoRepo, cuentaRepo);
    }

    private Cuenta buildCuenta() {
        return Cuenta.builder()
                .id(1L)
                .numeroCuenta("12345")
                .tipoCuenta("Ahorros")
                .saldoInicial(BigDecimal.valueOf(500))
                .estado(true)
                .clienteId(100L)
                .build();
    }

    private Movimiento buildMovimiento(Long cuentaId) {
        return Movimiento.builder()
                .id(1L)
                .fecha(LocalDateTime.now())
                .tipo(TipoMovimiento.DEPOSITO)
                .valor(BigDecimal.valueOf(100))
                .saldo(BigDecimal.valueOf(600))
                .cuentaId(cuentaId)
                .build();
    }

    private MovimientoDTO buildMovimientoDTO() {
        return MovimientoDTO.builder()
                .cuentaId(1L)
                .tipo("DEPOSITO")
                .valor(BigDecimal.valueOf(100))
                .build();
    }

    @Test
    void registrarMovimiento_depositoExitoso() {
        var dto = buildMovimientoDTO();
        var cuenta = buildCuenta();
        var movimiento = buildMovimiento(cuenta.getId());

        when(cuentaRepo.findById(1L)).thenReturn(Optional.of(cuenta));
        when(cuentaRepo.save(any(Cuenta.class))).thenReturn(cuenta);
        when(movimientoRepo.save(any(Movimiento.class))).thenReturn(movimiento);

        ApiResponse<MovimientoDTO> response = service.registrarMovimiento(dto);

        assertEquals("Movimiento registrado correctamente", response.getMessage());
        assertEquals(BigDecimal.valueOf(600), response.getData().getSaldo());
        verify(cuentaRepo, times(1)).save(any(Cuenta.class));
        verify(movimientoRepo, times(1)).save(any(Movimiento.class));
    }

    @Test
    void registrarMovimiento_cuentaNoExiste_deberiaLanzarExcepcion() {
        var dto = buildMovimientoDTO();

        when(cuentaRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CuentaNotFoundException.class,
                () -> service.registrarMovimiento(dto));
    }

    @Test
    void registrarMovimiento_cuentaInactiva_deberiaLanzarExcepcion() {
        var dto = buildMovimientoDTO();
        var cuenta = buildCuenta();
        cuenta.setEstado(false);

        when(cuentaRepo.findById(1L)).thenReturn(Optional.of(cuenta));

        assertThrows(CuentaInactivaException.class,
                () -> service.registrarMovimiento(dto));
    }

    @Test
    void registrarMovimiento_saldoInsuficiente_deberiaLanzarExcepcion() {
        var dto = MovimientoDTO.builder()
                .cuentaId(1L)
                .tipo("RETIRO")
                .valor(BigDecimal.valueOf(1000))
                .build();

        var cuenta = buildCuenta();

        when(cuentaRepo.findById(1L)).thenReturn(Optional.of(cuenta));

        assertThrows(SaldoNoDisponibleException.class,
                () -> service.registrarMovimiento(dto));
    }

    @Test
    void listarPorCuentaId_deberiaRetornarMovimientos() {
        var cuenta = buildCuenta();
        var movimiento = buildMovimiento(cuenta.getId());

        when(movimientoRepo.findByCuentaId(1L)).thenReturn(List.of(movimiento));
        when(cuentaRepo.findById(1L)).thenReturn(Optional.of(cuenta));

        var result = service.listarPorCuentaId(1L);

        assertEquals(1, result.size());
        assertEquals("Movimiento encontrado", result.get(0).getMessage());
        assertEquals("12345", result.get(0).getData().getNumeroCuenta());
    }

    @Test
    void listarTodos_deberiaRetornarMovimientos() {
        var cuenta = buildCuenta();
        var movimiento = buildMovimiento(cuenta.getId());

        when(movimientoRepo.findAll()).thenReturn(List.of(movimiento));
        when(cuentaRepo.findById(1L)).thenReturn(Optional.of(cuenta));

        var response = service.listarTodos();

        assertEquals("Listado de movimientos obtenido correctamente", response.getMessage());
        assertEquals(1, response.getData().size());
        assertEquals("12345", response.getData().get(0).getNumeroCuenta());
    }

    @Test
    void obtenerReporte_deberiaRetornarMovimientosEnRango() {
        var cuenta = buildCuenta();
        var movimiento = buildMovimiento(cuenta.getId());

        when(movimientoRepo.findByClienteAndFechaBetween(
                eq(100L),
                any(LocalDate.class),
                any(LocalDate.class)))
                .thenReturn(List.of(movimiento));
        when(cuentaRepo.findById(1L)).thenReturn(Optional.of(cuenta));

        var result = service.obtenerReporte(100L,
                LocalDate.now().minusDays(5),
                LocalDate.now());

        assertEquals(1, result.size());
        assertEquals("Movimiento en rango de fechas", result.get(0).getMessage());
        assertEquals("12345", result.get(0).getData().getNumeroCuenta());
    }
}
