package com.example.security.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
/**
 * Lo que el servidor responde si el usuario se autenticó correctamente: JWT.
 */
@Getter
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private String tokenType;
}
