package com.example.security.repository;

import com.example.security.entity.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    // Nuevo frente a 02-jwt: CustomUserDetailsService necesita buscar
    // por username, no solo por id.
    Optional<UserEntity> findByUsername(String username);
}
