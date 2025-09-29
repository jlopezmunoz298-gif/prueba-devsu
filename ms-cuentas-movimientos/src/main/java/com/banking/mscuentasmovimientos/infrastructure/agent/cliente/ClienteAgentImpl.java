package com.banking.mscuentasmovimientos.infrastructure.agent.cliente;

import com.banking.mscuentasmovimientos.application.dto.ApiResponse;
import com.banking.mscuentasmovimientos.application.port.output.agent.IClienteAgentPort;
import com.banking.mscuentasmovimientos.infrastructure.agent.cliente.dto.response.ClienteDTO;
import com.banking.mscuentasmovimientos.infrastructure.agent.cliente.exception.ClienteNotFoundException;
import com.banking.mscuentasmovimientos.infrastructurecross.application.Setting;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class ClienteAgentImpl implements IClienteAgentPort {

    private final RestTemplate restTemplate;
    private final Setting setting;

    private static final String OBTENER_CLIENTE_POR_IDENTIFICACION = "/{identificacion}";
    private static final String OBTENER_CLIENTE_POR_ID = "/id/{id}";
    private static final String IDENTIFICACION = "Identificacion ";
    private static final String ID = "ID ";


    @Override
    public ClienteDTO obtenerClientePorIdentificacion(String identificacion) {
        try {
            ApiResponse response = restTemplate.getForObject(
                    setting.getMsClientes() + OBTENER_CLIENTE_POR_IDENTIFICACION,
                    ApiResponse.class,
                    identificacion
            );

            if (response == null || response.getData() == null) {
                throw new ClienteNotFoundException(IDENTIFICACION + identificacion);
            }

            return mapToClienteDTO(response.getData());
        } catch (Exception e) {
            throw new ClienteNotFoundException(IDENTIFICACION + identificacion);
        }
    }

    @Override
    public ClienteDTO obtenerClientePorId(Long id) {
        try {
            ApiResponse response = restTemplate.getForObject(
                    setting.getMsClientes() + OBTENER_CLIENTE_POR_ID,
                    ApiResponse.class,
                    id
            );

            if (response == null || response.getData() == null) {
                throw new ClienteNotFoundException(ID + id);
            }

            return mapToClienteDTO(response.getData());
        } catch (Exception e) {
            throw new ClienteNotFoundException(ID + id);
        }
    }

    private ClienteDTO mapToClienteDTO(Object data) {
        var map = (java.util.LinkedHashMap<?, ?>) data;

        return ClienteDTO.builder()
                .id(((Number) map.get("id")).longValue())
                .identificacion((String) map.get("identificacion"))
                .nombre((String) map.get("nombre"))
                .genero((String) map.get("genero"))
                .edad((Integer) map.get("edad"))
                .direccion((String) map.get("direccion"))
                .telefono((String) map.get("telefono"))
                .estado((Boolean) map.get("estado"))
                .build();
    }
}
