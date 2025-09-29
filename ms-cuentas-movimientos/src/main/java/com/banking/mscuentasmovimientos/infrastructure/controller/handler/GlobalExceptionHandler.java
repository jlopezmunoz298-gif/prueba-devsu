package com.banking.mscuentasmovimientos.infrastructure.controller.handler;

import com.banking.mscuentasmovimientos.application.exception.ApplicationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<Map<String, Object>> handleApplicationException(ApplicationException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("codigoError", ex.getCodigoError());
        body.put("error", ex.getMessage());
        body.put("timestamp", LocalDateTime.now());
        body.put("status", ex.getHttpStatus().value());

        return ResponseEntity.status(ex.getHttpStatus()).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("codigoError", "INTERNAL_SERVER_ERROR");
        body.put("error", ex.getMessage());
        body.put("timestamp", LocalDateTime.now());
        body.put("status", 500);

        return ResponseEntity.internalServerError().body(body);
    }
}
