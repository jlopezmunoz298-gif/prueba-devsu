package com.banking.clientes.msclientes.infrastructure.repository.port;

import com.banking.clientes.msclientes.infrastructure.repository.entity.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteJpaRepository extends JpaRepository<ClienteEntity, Long> {
    ClienteEntity findByIdentificacion(String identificacion);
}
