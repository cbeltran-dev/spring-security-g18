package com.example.security.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;


/**
 * <h5>Manejador global de excepciones.</h5>
 *
 * <p>Aclaracion de seguridad util para entender el flujo:</p>
 *
 * <ul>
 *   <li>El 401 cuando entras a una ruta protegida SIN token lo decide la cadena
 *       de filtros de Spring Security, ANTES de llegar a un controlador (no pasa
 *       por aqui).</li>
 *   <li>El 401 de un LOGIN fallido (usuario/clave incorrectos) SI pasa por aqui,
 *       porque el authenticationManager lanza la excepcion DENTRO del controlador.</li>
 * </ul>
 */


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    // 401 -> credenciales invalidas en el login (usuario o clave incorrectos).
    // Esta excepcion la lanza el authenticationManager dentro del AuthController.
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuth(AuthenticationException e) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(), HttpStatus.UNAUTHORIZED.value(),
                "No autenticado", "Usuario o contrasena incorrectos.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErrorResponse> handleGeneral(Exception e) {
//        log.error("Error inesperado", e);
//        ErrorResponse error = new ErrorResponse(
//                LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
//                "Error interno", "Ocurrio un error inesperado. Contacte al administrador.");
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
//    }
}
