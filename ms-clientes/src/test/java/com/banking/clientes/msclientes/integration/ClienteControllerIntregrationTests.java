package com.banking.clientes.msclientes.integration;

import com.banking.clientes.msclientes.application.dto.ClienteDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ClienteControllerIntregrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void crearCliente_exitoso() throws Exception {
        ClienteDTO dto = ClienteDTO.builder()
                .nombre("Juan Pérez")
                .genero("M")
                .edad(30)
                .identificacion(String.valueOf(System.currentTimeMillis()))
                .direccion("Calle 123")
                .telefono("3200000000")
                .contrasenia("claveSegura")
                .estado(true)
                .build();

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Cliente creado correctamente")))
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.nombre", is("Juan Pérez")));
    }

    @Test
    void obtenerCliente_exitoso() throws Exception {
        String identificacion = String.valueOf(System.currentTimeMillis());

        ClienteDTO dto = ClienteDTO.builder()
                .nombre("Ana Gómez")
                .genero("F")
                .edad(28)
                .identificacion(identificacion)
                .direccion("Calle 45")
                .telefono("3100000000")
                .contrasenia("otraClave")
                .estado(true)
                .build();

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Cliente creado correctamente")))
                .andExpect(jsonPath("$.data.identificacion", is(identificacion)));

        mockMvc.perform(get("/clientes/{identificacion}", identificacion))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Cliente obtenido correctamente")))
                .andExpect(jsonPath("$.data.nombre", is("Ana Gómez")))
                .andExpect(jsonPath("$.data.identificacion", is(identificacion)));
    }

    @Test
    void listarClientes_exitoso() throws Exception {
        mockMvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Listado de clientes obtenido correctamente")))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void actualizarCliente_exitoso() throws Exception {
        ClienteDTO dto = ClienteDTO.builder()
                .nombre("Pedro")
                .genero("M")
                .edad(40)
                .identificacion(String.valueOf(System.currentTimeMillis()))
                .direccion("Calle 90")
                .telefono("3111111111")
                .contrasenia("clave")
                .estado(true)
                .build();

        String response = mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = objectMapper.readTree(response).path("data").path("id").asLong();

        dto.setNombre("Pedro Actualizado");

        mockMvc.perform(put("/clientes/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Cliente actualizado correctamente")))
                .andExpect(jsonPath("$.data.nombre", is("Pedro Actualizado")));
    }

    @Test
    void eliminarCliente_exitoso() throws Exception {
        ClienteDTO dto = ClienteDTO.builder()
                .nombre("Cliente a eliminar")
                .genero("M")
                .edad(25)
                .identificacion(String.valueOf(System.currentTimeMillis()))
                .direccion("Calle 99")
                .telefono("3222222222")
                .contrasenia("clave")
                .estado(true)
                .build();

        String response = mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = objectMapper.readTree(response).path("data").path("id").asLong();

        mockMvc.perform(delete("/clientes/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Cliente eliminado correctamente")))
                .andExpect(jsonPath("$.data.id", is(id.intValue())));
    }
}
