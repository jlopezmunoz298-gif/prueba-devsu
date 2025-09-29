package com.banking.mscuentasmovimientos.unit;

import com.banking.mscuentasmovimientos.application.CuentaServiceImpl;
import com.banking.mscuentasmovimientos.application.dto.ApiResponse;
import com.banking.mscuentasmovimientos.application.dto.CuentaDTO;
import com.banking.mscuentasmovimientos.application.exception.CuentaInactivaException;
import com.banking.mscuentasmovimientos.application.exception.CuentaNotFoundException;
import com.banking.mscuentasmovimientos.application.port.output.agent.IClienteAgentPort;
import com.banking.mscuentasmovimientos.application.port.output.db.CuentaRepositoryPort;
import com.banking.mscuentasmovimientos.domain.entity.Cuenta;
import com.banking.mscuentasmovimientos.infrastructure.agent.cliente.dto.response.ClienteDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CuentaServiceImplTests {

    private CuentaRepositoryPort repo;
    private IClienteAgentPort clienteAgent;
    private CuentaServiceImpl service;

    @BeforeEach
    void setUp() {
        repo = mock(CuentaRepositoryPort.class);
        clienteAgent = mock(IClienteAgentPort.class);
        service = new CuentaServiceImpl(repo, clienteAgent);
    }

    private Cuenta buildCuenta() {
        return Cuenta.builder()
                .id(1L)
                .numeroCuenta("12345")
                .tipoCuenta("Ahorros")
                .saldoInicial(BigDecimal.valueOf(500))
                .estado(true)
                .clienteId(10L)
                .build();
    }

    private CuentaDTO buildCuentaDTO() {
        return CuentaDTO.builder()
                .id(1L)
                .numeroCuenta("12345")
                .tipoCuenta("Ahorros")
                .saldoInicial(BigDecimal.valueOf(500))
                .estado(true)
                .clienteIdentificacion("CC123")
                .clienteId(10L)
                .nombreCliente("Juan Pérez")
                .build();
    }

    private ClienteDTO buildClienteDTO() {
        return ClienteDTO.builder()
                .id(10L)
                .identificacion("CC123")
                .nombre("Juan Pérez")
                .build();
    }

    @Test
    void crearCuenta_deberiaCrearCorrectamente() {
        var dto = buildCuentaDTO();
        var cuentaGuardada = buildCuenta();
        var cliente = buildClienteDTO();

        when(clienteAgent.obtenerClientePorIdentificacion("CC123"))
                .thenReturn(cliente);
        when(repo.save(any(Cuenta.class))).thenReturn(cuentaGuardada);

        ApiResponse<CuentaDTO> response = service.crearCuenta(dto);

        assertEquals("Cuenta creada correctamente", response.getMessage());
        assertNotNull(response.getData());
        assertEquals("CC123", response.getData().getClienteIdentificacion());

        verify(repo, times(1)).save(any(Cuenta.class));
    }

    @Test
    void obtenerCuentaPorId_cuentaActiva_deberiaDevolverla() {
        var cuenta = buildCuenta();
        var cliente = buildClienteDTO();

        when(repo.findById(1L)).thenReturn(Optional.of(cuenta));
        when(clienteAgent.obtenerClientePorId(10L)).thenReturn(cliente);

        ApiResponse<CuentaDTO> response = service.obtenerCuentaPorId(1L);

        assertEquals("Cuenta obtenida correctamente", response.getMessage());
        assertEquals("Juan Pérez", response.getData().getNombreCliente());
    }

    @Test
    void obtenerCuentaPorId_cuentaInactiva_deberiaLanzarExcepcion() {
        var cuenta = buildCuenta();
        cuenta.setEstado(false);

        when(repo.findById(1L)).thenReturn(Optional.of(cuenta));

        assertThrows(CuentaInactivaException.class,
                () -> service.obtenerCuentaPorId(1L));
    }

    @Test
    void obtenerCuentaPorId_cuentaNoExiste_deberiaLanzarExcepcion() {
        when(repo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CuentaNotFoundException.class,
                () -> service.obtenerCuentaPorId(1L));
    }

    @Test
    void obtenerTodasLasCuentas_deberiaFiltrarInactivas() {
        var cuentaActiva = buildCuenta();
        var cuentaInactiva = buildCuenta();
        cuentaInactiva.setEstado(false);
        var cliente = buildClienteDTO();

        when(repo.findAll()).thenReturn(List.of(cuentaActiva, cuentaInactiva));
        when(clienteAgent.obtenerClientePorId(10L)).thenReturn(cliente);

        var result = service.obtenerTodasLasCuentas();

        assertEquals(1, result.size());
        assertEquals("Cuenta encontrada", result.get(0).getMessage());
        assertEquals("Juan Pérez", result.get(0).getData().getNombreCliente());
    }

    @Test
    void actualizarCuenta_cuentaActiva_deberiaActualizarCorrectamente() {
        var existente = buildCuenta();
        var dto = buildCuentaDTO();
        var cliente = buildClienteDTO();

        when(repo.findById(1L)).thenReturn(Optional.of(existente));
        when(repo.save(any(Cuenta.class))).thenReturn(existente);
        when(clienteAgent.obtenerClientePorIdentificacion("CC123"))
                .thenReturn(cliente);

        var response = service.actualizarCuenta(1L, dto);

        assertEquals("Cuenta actualizada correctamente", response.getMessage());
        verify(repo, times(1)).save(existente);
    }

    @Test
    void actualizarCuenta_cuentaInactiva_deberiaLanzarExcepcion() {
        var existente = buildCuenta();
        existente.setEstado(false);
        var dto = buildCuentaDTO();

        when(repo.findById(1L)).thenReturn(Optional.of(existente));

        assertThrows(CuentaInactivaException.class,
                () -> service.actualizarCuenta(1L, dto));
    }

    @Test
    void eliminarCuenta_cuentaActiva_deberiaDeshabilitarla() {
        var existente = buildCuenta();
        var cliente = buildClienteDTO();

        when(repo.findById(1L)).thenReturn(Optional.of(existente));
        when(repo.save(any(Cuenta.class))).thenReturn(existente);
        when(clienteAgent.obtenerClientePorId(1L)).thenReturn(cliente);

        var response = service.eliminarCuenta(1L);

        assertEquals("Cuenta deshabilitada correctamente", response.getMessage());
        assertFalse(existente.getEstado());
    }

    @Test
    void eliminarCuenta_cuentaInactiva_deberiaLanzarExcepcion() {
        var existente = buildCuenta();
        existente.setEstado(false);

        when(repo.findById(1L)).thenReturn(Optional.of(existente));

        assertThrows(CuentaInactivaException.class,
                () -> service.eliminarCuenta(1L));
    }
}
