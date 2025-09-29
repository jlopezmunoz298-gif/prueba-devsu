package com.banking.mscuentasmovimientos.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ApiResponse <T> {
    private String message;
    private T data;
}
