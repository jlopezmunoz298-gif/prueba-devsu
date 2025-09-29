package com.banking.mscuentasmovimientos.integration;

import com.banking.mscuentasmovimientos.application.dto.CuentaDTO;
import com.banking.mscuentasmovimientos.application.port.output.agent.IClienteAgentPort;
import com.banking.mscuentasmovimientos.infrastructure.agent.cliente.dto.response.ClienteDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CuentaControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IClienteAgentPort clienteAgent;

    private static final String CLIENTE_IDENTIFICACION_EXISTENTE = "9";

    @BeforeEach
    void setupMock() {
        when(clienteAgent.obtenerClientePorIdentificacion(anyString()))
                .thenAnswer(inv -> ClienteDTO.builder()
                        .id(99L)
                        .identificacion(inv.getArgument(0))
                        .nombre("Mock Cliente")
                        .build());

        when(clienteAgent.obtenerClientePorId(anyLong()))
                .thenAnswer(inv -> ClienteDTO.builder()
                        .id(inv.getArgument(0))
                        .identificacion(CLIENTE_IDENTIFICACION_EXISTENTE)
                        .nombre("Mock Cliente")
                        .build());
    }

    private CuentaDTO buildCuentaDTO(String numeroCuenta) {
        return CuentaDTO.builder()
                .numeroCuenta(numeroCuenta)
                .tipoCuenta("Ahorros")
                .saldoInicial(BigDecimal.valueOf(500))
                .estado(true)
                .clienteIdentificacion(CLIENTE_IDENTIFICACION_EXISTENTE)
                .build();
    }

    private String uniqueNumeroCuenta() {
        return String.valueOf(System.currentTimeMillis());
    }

    @Test
    void crearCuenta_exitoso() throws Exception {
        CuentaDTO dto = buildCuentaDTO(uniqueNumeroCuenta());

        mockMvc.perform(post("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Cuenta creada correctamente")))
                .andExpect(jsonPath("$.data.id", notNullValue()))
                .andExpect(jsonPath("$.data.numeroCuenta", is(dto.getNumeroCuenta())));
    }

    @Test
    void obtenerCuenta_exitoso() throws Exception {
        CuentaDTO dto = buildCuentaDTO(uniqueNumeroCuenta());

        String response = mockMvc.perform(post("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = objectMapper.readTree(response).path("data").path("id").asLong();

        mockMvc.perform(get("/cuentas/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Cuenta obtenida correctamente")))
                .andExpect(jsonPath("$.data.numeroCuenta", is(dto.getNumeroCuenta())))
                .andExpect(jsonPath("$.data.clienteIdentificacion", is(CLIENTE_IDENTIFICACION_EXISTENTE)));
    }

    @Test
    void actualizarCuenta_exitoso() throws Exception {
        CuentaDTO dto = buildCuentaDTO(uniqueNumeroCuenta());

        String response = mockMvc.perform(post("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = objectMapper.readTree(response).path("data").path("id").asLong();

        dto.setNumeroCuenta(uniqueNumeroCuenta());

        mockMvc.perform(put("/cuentas/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Cuenta actualizada correctamente")))
                .andExpect(jsonPath("$.data.numeroCuenta", is(dto.getNumeroCuenta())));
    }

    @Test
    void eliminarCuenta_exitoso() throws Exception {
        CuentaDTO dto = buildCuentaDTO(uniqueNumeroCuenta());

        String response = mockMvc.perform(post("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = objectMapper.readTree(response).path("data").path("id").asLong();

        mockMvc.perform(delete("/cuentas/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Cuenta deshabilitada correctamente")))
                .andExpect(jsonPath("$.data.id", is(id.intValue())));
    }

}
