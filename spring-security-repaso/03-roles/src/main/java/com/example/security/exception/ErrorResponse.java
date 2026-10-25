package com.example.security.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Molde uniforme para devolver errores al cliente (timestamp, status, error,
 * mensaje). Asi quien consume la API siempre recibe la misma forma.
 */
@Data
@AllArgsConstructor
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String mensaje;
}
