package com.banking.clientes.msclientes.infrastructure.repository.adapter;

import com.banking.clientes.msclientes.application.port.output.db.ClienteRepositoryPort;
import com.banking.clientes.msclientes.domain.entity.Cliente;
import com.banking.clientes.msclientes.infrastructure.repository.entity.ClienteEntity;
import com.banking.clientes.msclientes.infrastructure.repository.port.ClienteJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ClienteRepositoryAdapter implements ClienteRepositoryPort {
    private final ClienteJpaRepository jpa;

    @Override
    public Cliente save(Cliente c) {
        ClienteEntity saved = jpa.save(toEntity(c));
        return toDomain(saved);
    }

    @Override
    public Optional<Cliente> findById(Long id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Cliente> findByIdentificacion(String identificacion) {
        ClienteEntity entity = jpa.findByIdentificacion(identificacion);
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    @Override
    public List<Cliente> findAll() {
        return jpa.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        jpa.deleteById(id);
    }

    // ---------- Mapping ----------
    private ClienteEntity toEntity(Cliente c) {
        if (c == null) return null;
        return ClienteEntity.builder()
                .id(c.getId())
                .identificacion(c.getIdentificacion())
                .nombre(c.getNombre())
                .genero(c.getGenero())
                .edad(c.getEdad())
                .direccion(c.getDireccion())
                .telefono(c.getTelefono())
                .contrasena(c.getContrasenia())
                .estado(c.getEstado())
                .build();
    }

    private Cliente toDomain(ClienteEntity e) {
        if (e == null) return null;
        return Cliente.builder()
                .id(e.getId())
                .identificacion(e.getIdentificacion())
                .nombre(e.getNombre())
                .genero(e.getGenero())
                .edad(e.getEdad())
                .direccion(e.getDireccion())
                .telefono(e.getTelefono())
                .contrasenia(e.getContrasena())
                .estado(e.getEstado())
                .build();
    }
}
