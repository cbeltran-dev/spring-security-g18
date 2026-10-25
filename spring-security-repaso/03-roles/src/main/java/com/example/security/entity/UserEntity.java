package com.example.security.entity;

import jakarta.persistence.*;
import lombok.*;

// NOTA para la sesión: esta entidad es dato de NEGOCIO, no tiene relación
// con los usuarios que autentican por HTTP Basic en SecurityConfig.
// Esa separación es intencional en este proyecto — se une recién en 02-jwt.
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "password")
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Roles role;
}
