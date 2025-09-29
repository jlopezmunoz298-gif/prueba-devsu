package com.banking.mscuentasmovimientos.infrastructure.controller;

import com.banking.mscuentasmovimientos.application.dto.ApiResponse;
import com.banking.mscuentasmovimientos.application.dto.CuentaDTO;
import com.banking.mscuentasmovimientos.application.port.interactor.ICuentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cuentas")
@RequiredArgsConstructor
public class CuentaController {

    private final ICuentaService cuentaService;

    @PostMapping
    public ResponseEntity<ApiResponse<CuentaDTO>> crear(@RequestBody CuentaDTO dto) {
        return ResponseEntity.ok(cuentaService.crearCuenta(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CuentaDTO>> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(cuentaService.obtenerCuentaPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<ApiResponse<CuentaDTO>>> listar() {
        return ResponseEntity.ok(cuentaService.obtenerTodasLasCuentas());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CuentaDTO>> actualizar(@PathVariable Long id,
                                                             @RequestBody CuentaDTO dto) {
        return ResponseEntity.ok(cuentaService.actualizarCuenta(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<CuentaDTO>> eliminar(@PathVariable Long id) {
        return ResponseEntity.ok(cuentaService.eliminarCuenta(id));
    }
}
