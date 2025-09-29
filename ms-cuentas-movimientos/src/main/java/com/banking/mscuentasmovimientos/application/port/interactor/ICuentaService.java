package com.banking.mscuentasmovimientos.application.port.interactor;

import com.banking.mscuentasmovimientos.application.dto.ApiResponse;
import com.banking.mscuentasmovimientos.application.dto.CuentaDTO;

import java.util.List;

public interface ICuentaService {
    ApiResponse<CuentaDTO> crearCuenta(CuentaDTO cuentaDTO);
    ApiResponse<CuentaDTO> obtenerCuentaPorId(Long id);
    List<ApiResponse<CuentaDTO>> obtenerTodasLasCuentas();
    ApiResponse<CuentaDTO> actualizarCuenta(Long id, CuentaDTO cuentaDTO);
    ApiResponse<CuentaDTO> eliminarCuenta(Long id);
}
