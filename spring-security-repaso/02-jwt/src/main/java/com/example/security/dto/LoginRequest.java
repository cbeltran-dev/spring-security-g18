package com.example.security.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
/**
 * Lo que el cliente envia al hacer login: usuario y clave.
 */
@Getter
@Setter
public class LoginRequest {

    @NotBlank(message = "El username es obligatorio")
    private String username;

    @NotBlank(message = "El password es obligatorio")
    private String password;
}
