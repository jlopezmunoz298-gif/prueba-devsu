package com.banking.clientes.msclientes.application;

import com.banking.clientes.msclientes.application.dto.ApiResponse;
import com.banking.clientes.msclientes.application.dto.ClienteDTO;
import com.banking.clientes.msclientes.application.exception.ApplicationException;
import com.banking.clientes.msclientes.application.exception.ClienteAlreadyExistsException;
import com.banking.clientes.msclientes.application.exception.ClienteNotFoundException;
import com.banking.clientes.msclientes.application.port.interactor.IClienteService;
import com.banking.clientes.msclientes.application.port.output.db.ClienteRepositoryPort;
import com.banking.clientes.msclientes.domain.entity.Cliente;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements IClienteService {

    private final ClienteRepositoryPort repo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ApiResponse<ClienteDTO> crear(ClienteDTO dto) {
        repo.findByIdentificacion(dto.getIdentificacion()).ifPresent(c -> {
            throw new ClienteAlreadyExistsException(dto.getIdentificacion());
        });

        dto.setContrasenia(passwordEncoder.encode(dto.getContrasenia()));

        Cliente saved = repo.save(toDomain(dto));
        return new ApiResponse<>("Cliente creado correctamente", toDTO(saved));
    }

    @Override
    public ApiResponse<ClienteDTO> obtener(Long identificacion) {
        Cliente c = repo.findByIdentificacion(String.valueOf(identificacion))
                .orElseThrow(() -> new ClienteNotFoundException(identificacion));
        return new ApiResponse<>("Cliente obtenido correctamente", toDTO(c));
    }

    @Override
    public ApiResponse<ClienteDTO> obtenerPorId(Long id) {
        Cliente c = repo.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException(id));
        return new ApiResponse<>("Cliente obtenido correctamente", toDTO(c));
    }

    @Override
    public ApiResponse<List<ClienteDTO>> listar() {
        List<ClienteDTO> clientes = repo.findAll().stream().map(this::toDTO).toList();
        return new ApiResponse<>("Listado de clientes obtenido correctamente", clientes);
    }

    @Override
    public ApiResponse<ClienteDTO> actualizar(Long id, ClienteDTO dto) {
        Cliente existente = repo.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException(id));

        actualizarFrom(existente, dto);
        Cliente actualizado = repo.save(existente);

        return new ApiResponse<>("Cliente actualizado correctamente", toDTO(actualizado));
    }

    @Override
    public ApiResponse<ClienteDTO> eliminar(Long id) {
        Cliente existente = repo.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException(id));

        repo.deleteById(id);
        return new ApiResponse<>("Cliente eliminado correctamente", toDTO(existente));
    }

    // ------- mapping --------
    private Cliente toDomain(ClienteDTO d) {
        if (d == null) return null;
        return Cliente.builder()
                .id(d.getId())
                .nombre(d.getNombre())
                .genero(d.getGenero())
                .edad(d.getEdad())
                .identificacion(d.getIdentificacion())
                .direccion(d.getDireccion())
                .telefono(d.getTelefono())
                .contrasenia(d.getContrasenia())
                .estado(d.getEstado())
                .build();
    }

    private ClienteDTO toDTO(Cliente c) {
        if (c == null) return null;
        return ClienteDTO.builder()
                .id(c.getId())
                .nombre(c.getNombre())
                .genero(c.getGenero())
                .edad(c.getEdad())
                .identificacion(c.getIdentificacion())
                .direccion(c.getDireccion())
                .telefono(c.getTelefono())
                .estado(c.getEstado())
                .build();
    }

    private void actualizarFrom(Cliente existente, ClienteDTO dto) {
        existente.setNombre(dto.getNombre());
        existente.setGenero(dto.getGenero());
        existente.setEdad(dto.getEdad());
        existente.setIdentificacion(dto.getIdentificacion());
        existente.setDireccion(dto.getDireccion());
        existente.setTelefono(dto.getTelefono());
        existente.setContrasenia(dto.getContrasenia());
        existente.setEstado(dto.getEstado());
    }
}
