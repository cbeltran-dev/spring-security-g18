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


/**
 * <h4>AuthController · LA PUERTA DE ENTRADA (login) Y EL "QUIEN SOY"</h4>
 *
 * Tiene dos endpoints:
 *
 * <h5>POST /api/v1/auth/login (PUBLICO)</h5>
 *
 * Recibe usuario y clave, los valida y, si todo bien, devuelve un TOKEN.
 * Es el unico lugar donde se usa la contrasena. Despues de esto, el cliente
 * ya no la vuelve a mandar: lleva el token.
 *
 * <h5>GET /api/v1/auth/me (PROTEGIDO)</h5>
 *
 * Devuelve quien esta autenticado (segun el token). Util para comprobar
 * en vivo que el token funciona.
 */

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

/**
 * <h6>LOGIN. Aqui ocurre la magia. Pasa de "usuario/clave -> token":</h6>
 *
 * <ol>
 *   <li>
 *     {@link AuthService#login(LoginRequest)} valida usuario y clave contra el
 *     UserDetailsService + BCrypt. Si NO coinciden, lanza una
 *     AuthenticationException. (la atrapamos en el GlobalExceptionHandler
 *     y respondemos 401).
 *   </li>
 *   <li>
 *     Si pasó, cargamos el usuario y generamos su token con el JwtService.
 *   </li>
 *   <li>
 *     Devolvemos el token; el cliente lo guardara y lo usara en cada peticion.
 *   </li>
 * </ol>
 */

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.login(request));
    }

    /**
     * GET /api/v1/auth/me  ->  quien esta autenticado (segun el token).
     * <p>
     * El objeto Authentication lo inyecta Spring; lo dejo NUESTRO filtro al
     * validar el token. Si llegamos aqui, es porque el token era valido.
     */
    @GetMapping("/me")
    public Map<String, Object> quienSoy(Authentication authentication) {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("usuario", authentication.getName());          // el username del que se logueo
        info.put("roles", authentication.getAuthorities());     // sus roles/permisos
        info.put("autenticado", authentication.isAuthenticated());
        return info;
    }
}
