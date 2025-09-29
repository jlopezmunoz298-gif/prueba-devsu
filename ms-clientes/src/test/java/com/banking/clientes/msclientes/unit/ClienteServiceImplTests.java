package com.banking.clientes.msclientes.unit;

import com.banking.clientes.msclientes.application.ClienteServiceImpl;
import com.banking.clientes.msclientes.application.dto.ApiResponse;
import com.banking.clientes.msclientes.application.dto.ClienteDTO;
import com.banking.clientes.msclientes.application.exception.ClienteAlreadyExistsException;
import com.banking.clientes.msclientes.application.exception.ClienteNotFoundException;
import com.banking.clientes.msclientes.application.port.output.db.ClienteRepositoryPort;
import com.banking.clientes.msclientes.domain.entity.Cliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ClienteServiceImplTests {
    @Mock
    private ClienteRepositoryPort repo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ClienteServiceImpl service;

    private Cliente cliente;
    private ClienteDTO clienteDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        cliente = Cliente.builder()
                .id(1L)
                .nombre("Juan Pérez")
                .genero("M")
                .edad(30)
                .identificacion("123456789")
                .direccion("Calle 123")
                .telefono("3200000000")
                .contrasenia("encodedPassword")
                .estado(true)
                .build();

        clienteDTO = ClienteDTO.builder()
                .id(1L)
                .nombre("Juan Pérez")
                .genero("M")
                .edad(30)
                .identificacion("123456789")
                .direccion("Calle 123")
                .telefono("3200000000")
                .contrasenia("plainPassword")
                .estado(true)
                .build();
    }

    @Test
    void crearCliente_exitoso() {
        when(repo.findByIdentificacion(clienteDTO.getIdentificacion())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(repo.save(any())).thenReturn(cliente);

        ApiResponse<ClienteDTO> response = service.crear(clienteDTO);

        assertEquals("Cliente creado correctamente", response.getMessage());
        assertNotNull(response.getData());
        verify(repo, times(1)).save(any());
    }

    @Test
    void crearCliente_yaExiste() {
        when(repo.findByIdentificacion(clienteDTO.getIdentificacion())).thenReturn(Optional.of(cliente));

        assertThrows(ClienteAlreadyExistsException.class, () -> service.crear(clienteDTO));
        verify(repo, never()).save(any());
    }

    @Test
    void obtenerCliente_exitoso() {
        when(repo.findByIdentificacion(String.valueOf(987654321))).thenReturn(Optional.of(cliente));

        ApiResponse<ClienteDTO> response = service.obtener(987654321L);

        assertEquals("Cliente obtenido correctamente", response.getMessage());
        assertEquals("Juan Pérez", response.getData().getNombre());
    }

    @Test
    void obtenerCliente_noExiste() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ClienteNotFoundException.class, () -> service.obtener(99L));
    }

    @Test
    void listarClientes_exitoso() {
        when(repo.findAll()).thenReturn(List.of(cliente));

        ApiResponse<List<ClienteDTO>> response = service.listar();

        assertEquals(1, response.getData().size());
        assertEquals("Listado de clientes obtenido correctamente", response.getMessage());
    }

    @Test
    void actualizarCliente_exitoso() {
        when(repo.findById(1L)).thenReturn(Optional.of(cliente));
        when(repo.save(any())).thenReturn(cliente);

        ApiResponse<ClienteDTO> response = service.actualizar(1L, clienteDTO);

        assertEquals("Cliente actualizado correctamente", response.getMessage());
        verify(repo, times(1)).save(any());
    }

    @Test
    void eliminarCliente_exitoso() {
        when(repo.findById(1L)).thenReturn(Optional.of(cliente));
        doNothing().when(repo).deleteById(1L);

        ApiResponse<ClienteDTO> response = service.eliminar(1L);

        assertEquals("Cliente eliminado correctamente", response.getMessage());
        verify(repo, times(1)).deleteById(1L);
    }

    @Test
    void eliminarCliente_noExiste() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ClienteNotFoundException.class, () -> service.eliminar(99L));
    }
}
