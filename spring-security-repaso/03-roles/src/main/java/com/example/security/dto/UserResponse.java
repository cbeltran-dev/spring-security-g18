package com.example.security.dto;

import com.example.security.entity.Roles;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String username;
    private Roles role;
    // A propósito NO tiene password. El punto a remarcar en la sesión:
    // el Entity puede tener el campo, el Response nunca lo devuelve.
}
