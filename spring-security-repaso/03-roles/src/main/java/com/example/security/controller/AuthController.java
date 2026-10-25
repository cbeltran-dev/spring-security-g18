package com.example.security.controller;

import com.example.security.dto.LoginRequest;
import com.example.security.dto.LoginResponse;
import com.example.security.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.login(request));
    }

    @GetMapping("/me")
    public Map<String, Object> quienSoy(Authentication authentication) {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("usuario", authentication.getName());          // el username del que se logueo
        info.put("roles", authentication.getAuthorities());     // sus roles/permisos
        info.put("autenticado", authentication.isAuthenticated());
        return info;
    }
}
