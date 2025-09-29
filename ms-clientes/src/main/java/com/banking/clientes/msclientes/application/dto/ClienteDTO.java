package com.banking.clientes.msclientes.application.dto;

import lombok.*;
@Getter
@Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class ClienteDTO {
    private Long id;
    private String identificacion;
    private String nombre;
    private String genero;
    private int edad;
    private String direccion;
    private String telefono;
    private String contrasenia;
    private Boolean estado;
}
