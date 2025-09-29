package com.banking.mscuentasmovimientos.application.port.output.agent;

import com.banking.mscuentasmovimientos.infrastructure.agent.cliente.dto.response.ClienteDTO;

public interface IClienteAgentPort {
    ClienteDTO obtenerClientePorIdentificacion(String identificacion);
    ClienteDTO obtenerClientePorId(Long id);
}
