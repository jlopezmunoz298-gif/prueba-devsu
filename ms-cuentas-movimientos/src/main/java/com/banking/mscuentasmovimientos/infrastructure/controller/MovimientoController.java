package com.banking.mscuentasmovimientos.infrastructure.controller;

import com.banking.mscuentasmovimientos.application.dto.ApiResponse;
import com.banking.mscuentasmovimientos.application.dto.MovimientoDTO;
import com.banking.mscuentasmovimientos.application.port.interactor.IMovimientoService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/movimientos")
@RequiredArgsConstructor
public class MovimientoController {

    private final IMovimientoService movimientoService;

    @PostMapping
    public ResponseEntity<ApiResponse<MovimientoDTO>> registrar(@RequestBody MovimientoDTO dto) {
        return ResponseEntity.ok(movimientoService.registrarMovimiento(dto));
    }

    @GetMapping("/cuenta/{cuentaId}")
    public ResponseEntity<List<ApiResponse<MovimientoDTO>>> listarPorCuenta(@PathVariable Long cuentaId) {
        return ResponseEntity.ok(movimientoService.listarPorCuentaId(cuentaId));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MovimientoDTO>>> listarTodos() {
        return ResponseEntity.ok(movimientoService.listarTodos());
    }

    @GetMapping("/reporte")
    public ResponseEntity<List<ApiResponse<MovimientoDTO>>> obtenerReporte(
            @RequestParam Long clienteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {

        return ResponseEntity.ok(movimientoService.obtenerReporte(clienteId, desde, hasta));
    }
}
