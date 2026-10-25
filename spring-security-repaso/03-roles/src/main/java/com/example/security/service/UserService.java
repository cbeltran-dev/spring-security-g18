package com.example.security.service;

import com.example.security.dto.UserRequest;
import com.example.security.dto.UserResponse;
import com.example.security.entity.UserEntity;
import com.example.security.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserResponse> listar() {
        return userRepository.findAll().stream()
                .map(this::aResponse)
                .toList();
    }

    public UserResponse crear(UserRequest request) {
        UserEntity user = new UserEntity();
        user.setUsername(request.getUsername());
        // DIFERENCIA frente a 02-jwt: ahora SÍ ciframos el password antes
        // de guardar. Sin esto, CustomUserDetailsService + BCrypt nunca
        // podrían validar un login real contra esta tabla.
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        UserEntity guardado = userRepository.save(user);
        return aResponse(guardado);
    }

    // Mapper manual
    private UserResponse aResponse(UserEntity user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getRole());
    }
}
