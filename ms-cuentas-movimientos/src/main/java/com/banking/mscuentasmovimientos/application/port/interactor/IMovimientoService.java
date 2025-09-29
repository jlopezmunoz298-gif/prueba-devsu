package com.banking.mscuentasmovimientos.application.port.interactor;

import com.banking.mscuentasmovimientos.application.dto.ApiResponse;
import com.banking.mscuentasmovimientos.application.dto.MovimientoDTO;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.util.List;

public interface IMovimientoService {
    ApiResponse<MovimientoDTO> registrarMovimiento(MovimientoDTO movimientoDTO);
    List<ApiResponse<MovimientoDTO>> listarPorCuentaId(Long cuentaId);
    ApiResponse<List<MovimientoDTO>> listarTodos();
    List<ApiResponse<MovimientoDTO>> obtenerReporte(Long clienteId, LocalDate desde, LocalDate hasta);
}
