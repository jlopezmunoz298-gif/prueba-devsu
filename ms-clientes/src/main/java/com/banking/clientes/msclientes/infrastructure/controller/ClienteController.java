package com.banking.clientes.msclientes.infrastructure.controller;

import com.banking.clientes.msclientes.application.dto.ApiResponse;
import com.banking.clientes.msclientes.application.dto.ClienteDTO;
import com.banking.clientes.msclientes.application.port.interactor.IClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final IClienteService service;

    @PostMapping
    public ResponseEntity<ApiResponse<ClienteDTO>> crear(@Valid @RequestBody ClienteDTO dto) {
        return ResponseEntity.ok(service.crear(dto));
    }

    @GetMapping("/{identificacion}")
    public ResponseEntity<ApiResponse<ClienteDTO>> obtener(@PathVariable String identificacion) {
        return ResponseEntity.ok(service.obtener(Long.valueOf(identificacion)));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<ApiResponse<ClienteDTO>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ClienteDTO>>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ClienteDTO>> actualizar(@PathVariable Long id,
                                                              @Valid @RequestBody ClienteDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<ClienteDTO>> eliminar(@PathVariable Long id) {
        return ResponseEntity.ok(service.eliminar(id));
    }
}
