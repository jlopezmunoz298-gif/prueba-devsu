package com.banking.mscuentasmovimientos;

import com.banking.mscuentasmovimientos.application.port.output.agent.IClienteAgentPort;
import com.banking.mscuentasmovimientos.infrastructure.agent.cliente.dto.response.ClienteDTO;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class ClienteAgentMockConfig {

    @Bean
    @Primary
    public IClienteAgentPort clienteAgentPort() {
        return new IClienteAgentPort() {
            @Override
            public ClienteDTO obtenerClientePorIdentificacion(String identificacion) {
                return ClienteDTO.builder()
                        .id(99L)
                        .identificacion(identificacion)
                        .nombre("Mock Cliente")
                        .build();
            }

            @Override
            public ClienteDTO obtenerClientePorId(Long id) {
                return ClienteDTO.builder()
                        .id(id)
                        .identificacion("9999")
                        .nombre("Mock Cliente")
                        .build();
            }
        };
    }
}
