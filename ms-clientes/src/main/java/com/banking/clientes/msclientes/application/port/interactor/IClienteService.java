package com.banking.clientes.msclientes.application.port.interactor;

import com.banking.clientes.msclientes.application.dto.ApiResponse;
import com.banking.clientes.msclientes.application.dto.ClienteDTO;

import java.util.List;

public interface IClienteService {
    ApiResponse<ClienteDTO> crear(ClienteDTO dto);
    ApiResponse<ClienteDTO> obtener(Long identificacion);
    ApiResponse<ClienteDTO> obtenerPorId(Long id);
    ApiResponse<List<ClienteDTO>> listar();
    ApiResponse<ClienteDTO> actualizar(Long id, ClienteDTO dto);
    ApiResponse<ClienteDTO> eliminar(Long id);

}
