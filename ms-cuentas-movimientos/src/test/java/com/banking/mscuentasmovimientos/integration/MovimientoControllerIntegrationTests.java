package com.banking.mscuentasmovimientos.integration;

import com.banking.mscuentasmovimientos.ClienteAgentMockConfig;
import com.banking.mscuentasmovimientos.application.dto.CuentaDTO;
import com.banking.mscuentasmovimientos.application.dto.MovimientoDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(ClienteAgentMockConfig.class)
public class MovimientoControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String CLIENTE_IDENTIFICACION_EXISTENTE = "9";

    private CuentaDTO buildCuentaDTO(String numeroCuenta) {
        return CuentaDTO.builder()
                .numeroCuenta(numeroCuenta)
                .tipoCuenta("Ahorros")
                .saldoInicial(BigDecimal.valueOf(1000))
                .estado(true)
                .clienteIdentificacion(CLIENTE_IDENTIFICACION_EXISTENTE)
                .build();
    }

    private MovimientoDTO buildMovimientoDTO(Long cuentaId, String tipo, BigDecimal valor) {
        return MovimientoDTO.builder()
                .cuentaId(cuentaId)
                .tipo(tipo)
                .valor(valor)
                .build();
    }

    @Test
    void registrarMovimiento_exitoso() throws Exception {
        CuentaDTO cuentaDTO = buildCuentaDTO("CTA-" + System.currentTimeMillis());
        String cuentaResponse = mockMvc.perform(post("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cuentaDTO)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Long cuentaId = objectMapper.readTree(cuentaResponse).path("data").path("id").asLong();

        MovimientoDTO movimientoDTO = buildMovimientoDTO(cuentaId, "DEPOSITO", BigDecimal.valueOf(200));

        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movimientoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Movimiento registrado correctamente")))
                .andExpect(jsonPath("$.data.id", notNullValue()))
                .andExpect(jsonPath("$.data.cuentaId", is(cuentaId.intValue())))
                .andExpect(jsonPath("$.data.tipo", is("DEPOSITO")))
                .andExpect(jsonPath("$.data.saldo", is(1200)));
    }

    @Test
    void listarPorCuenta_exitoso() throws Exception {
        CuentaDTO cuentaDTO = buildCuentaDTO("CTA-" + System.currentTimeMillis());
        String cuentaResponse = mockMvc.perform(post("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cuentaDTO)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Long cuentaId = objectMapper.readTree(cuentaResponse).path("data").path("id").asLong();

        MovimientoDTO movimientoDTO = buildMovimientoDTO(cuentaId, "DEPOSITO", BigDecimal.valueOf(100));
        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movimientoDTO)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/movimientos/cuenta/{cuentaId}", cuentaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].message", is("Movimiento encontrado")));
    }

    @Test
    void listarTodos_exitoso() throws Exception {
        mockMvc.perform(get("/movimientos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Listado de movimientos obtenido correctamente")))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void obtenerReporte_exitoso() throws Exception {

        CuentaDTO cuentaDTO = buildCuentaDTO("CTA-" + System.currentTimeMillis());
        String cuentaResponse = mockMvc.perform(post("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cuentaDTO)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Long cuentaId = objectMapper.readTree(cuentaResponse).path("data").path("id").asLong();

        MovimientoDTO movimientoDTO = buildMovimientoDTO(cuentaId, "DEPOSITO", BigDecimal.valueOf(150));
        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movimientoDTO)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/movimientos/reporte")
                        .param("clienteId", "99")
                        .param("desde", LocalDate.now().minusDays(1).toString())
                        .param("hasta", LocalDate.now().plusDays(1).toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].message", is("Movimiento en rango de fechas")));
    }
}
