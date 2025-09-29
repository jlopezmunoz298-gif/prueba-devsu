package com.banking.clientes.msclientes.domain.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
public class Cliente extends Persona {
    private Long id;
    private String contrasenia;
    private Boolean estado;
}

