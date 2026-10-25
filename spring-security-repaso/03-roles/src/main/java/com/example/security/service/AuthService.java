package com.example.security.service;

import com.example.security.dto.LoginRequest;
import com.example.security.dto.LoginResponse;
import com.example.security.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public LoginResponse login(LoginRequest request) {

        // 1. Validar usuario y clave. Si falla, lanza excepcion aqui.
        authenticationManager.authenticate( new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()));

        // 2. Credenciales correctas: generamos el token.
        UserDetails usuario = userDetailsService.loadUserByUsername(request.getUsername());
        String token = jwtService.generateToken(usuario);

        // 3. Respondemos el token.
        return new LoginResponse(token, "Bearer");
    }
}
