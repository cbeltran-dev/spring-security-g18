package com.example.security.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
/**
 * Controlador publico.
 *Para llegar a este recurso, la peticion sigue pasando por la
 * cadena de filtros de Sprig Security con la diferencia de que
 * las reglas de autorizacion permiten acceder a este sin exigir autenticacion
 */
@RestController
@RequestMapping("/api/v1/public")
public class PublicController {

    @GetMapping("/ping")
    public Map<String, String> ping() {
        return Map.of(
                "estado", "ok",
                "mensaje", "Este endpoint es publico: no necesita autenticacion"
        );
    }
}
